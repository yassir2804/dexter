package com.dexter.backend.service

import com.dexter.backend.client.AiPlanRequest
import com.dexter.backend.client.AiServiceClient
import com.dexter.backend.dto.MatchDetailResult
import com.dexter.backend.dto.MatchResult
import com.dexter.backend.dto.MatchInput
import com.dexter.backend.dto.MatchSummaryResult
import com.dexter.backend.entity.Match
import com.dexter.backend.entity.MatchStatus
import com.dexter.backend.entity.Notification
import com.dexter.backend.entity.NotificationType
import com.dexter.backend.entity.Session
import com.dexter.backend.entity.SessionStatus
import com.dexter.backend.entity.SkillRole
import com.dexter.backend.entity.User
import com.dexter.backend.repository.AvailabilityRepository
import com.dexter.backend.repository.MatchRepository
import com.dexter.backend.repository.NotificationRepository
import com.dexter.backend.repository.SessionRepository
import com.dexter.backend.repository.SkillRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class AbstractMatchService(
    @Autowired val matchRepository: MatchRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val skillRepository: SkillRepository,
    @Autowired val sessionRepository: SessionRepository,
    @Autowired val notificationRepository: NotificationRepository,
    @Autowired val availabilityRepository: AvailabilityRepository,
    @Autowired val aiServiceClient: AiServiceClient,
) : MatchService {

    override fun findByUser(userId: Long): List<MatchSummaryResult> =
        matchRepository.findByInitiatorIdOrReceiverId(userId, userId)
            .map { toSummary(it, userId) }

    override fun findById(matchId: Long): MatchResult {
        val match = matchRepository.findById(matchId).orElseThrow {
            NoSuchElementException("Match $matchId not found")
        }
        return toDto(match)
    }

    override fun create(request: MatchInput): MatchSummaryResult {
        if (request.initiatorId == request.receiverId)
            throw IllegalArgumentException("A user cannot create a match with themselves")

        val initiator = userRepository.findById(request.initiatorId)
            .orElseThrow { NoSuchElementException("User ${request.initiatorId} not found") }
        val receiver = userRepository.findById(request.receiverId)
            .orElseThrow { NoSuchElementException("User ${request.receiverId} not found") }

        val existing = matchRepository.findByInitiatorIdOrReceiverId(request.initiatorId, request.initiatorId)
            .filter { m ->
                val otherId = if (m.initiator?.id == request.initiatorId) m.receiver?.id else m.initiator?.id
                otherId == request.receiverId && m.status != MatchStatus.REJECTED && m.status != MatchStatus.COMPLETED
            }
        if (existing.isNotEmpty())
            throw IllegalArgumentException("A match between these users already exists")

        val score = calculateCompatibility(initiator, receiver)

        val match = matchRepository.save(Match(
            initiator = initiator,
            receiver = receiver,
            compatibilityScore = score,
            status = MatchStatus.PENDING,
            initiatorSkill = request.initiatorSkill,
            receiverSkill = request.receiverSkill,
            createdAt = Date(),
        ))

        notificationRepository.save(Notification(
            user = receiver,
            referenceId = match.id,
            title = "Nuevo match",
            body = "${initiator.firstName} quiere practicar contigo",
            notificationType = NotificationType.NEW_MATCH,
            isRead = false,
            createdAt = Date(),
        ))

        return toSummary(match, request.initiatorId)
    }

    @Transactional
    override fun accept(id: Long, viewerUserId: Long, initiatorSkill: String?, timezone: String?): MatchSummaryResult {
        val match = matchRepository.findById(id)
            .orElseThrow { NoSuchElementException("Match $id not found") }

        if (match.status != MatchStatus.PENDING)
            throw IllegalArgumentException("Match $id is not in PENDING state (current: ${match.status})")

        match.status = MatchStatus.ACCEPTED
        if (initiatorSkill != null) match.initiatorSkill = initiatorSkill
        ensureExchangeSkills(match)
        val saved = matchRepository.save(match)

        generateSessions(saved, timezone)

        // Contar el match en el historial de ambos participantes.
        listOfNotNull(match.initiator, match.receiver).forEach { u ->
            u.totalMatches = (u.totalMatches ?: 0) + 1
            u.updatedAt = Date()
            userRepository.save(u)
        }

        match.initiator?.let { initiator ->
            match.receiver?.let { receiver ->
                notificationRepository.save(Notification(
                    user = initiator,
                    referenceId = match.id,
                    title = "Match aceptado",
                    body = "${receiver.firstName} aceptó tu solicitud",
                    notificationType = NotificationType.NEW_MATCH,
                    isRead = false,
                    createdAt = Date(),
                ))
            }
        }

        return toSummary(saved, viewerUserId)
    }

    override fun reject(id: Long) {
        val match = matchRepository.findById(id)
            .orElseThrow { NoSuchElementException("Match $id not found") }
        if (match.status != MatchStatus.PENDING)
            throw IllegalArgumentException("Match $id is not in PENDING state (current: ${match.status})")
        match.status = MatchStatus.REJECTED
        matchRepository.save(match)
    }

    @Transactional
    override fun complete(id: Long): MatchSummaryResult {
        val match = matchRepository.findById(id)
            .orElseThrow { NoSuchElementException("Match $id not found") }
        val initiatorId = match.initiator?.id ?: 0L
        if (match.status == MatchStatus.COMPLETED) {
            return toSummary(match, initiatorId)
        }
        if (match.status != MatchStatus.ACCEPTED)
            throw IllegalArgumentException("Match $id is not in ACCEPTED state (current: ${match.status})")
        match.status = MatchStatus.COMPLETED
        val saved = matchRepository.save(match)
        saved.initiator?.let { notifyMatchCompleted(saved, it) }
        saved.receiver?.let { notifyMatchCompleted(saved, it) }
        return toSummary(saved, initiatorId)
    }

    private fun notifyMatchCompleted(match: Match, user: User) {
        val otherName = if (user.id == match.initiator?.id)
            match.receiver?.firstName else match.initiator?.firstName
        notificationRepository.save(Notification(
            user = user,
            referenceId = match.id,
            title = "Plan finalizado",
            body = "Tu plan de intercambio con ${otherName ?: "tu match"} ha sido completado",
            notificationType = NotificationType.NEW_MATCH,
            isRead = false,
            createdAt = Date(),
        ))
    }

    override fun ensureSessions(matchId: Long) {
        val match = matchRepository.findById(matchId)
            .orElseThrow { NoSuchElementException("Match $matchId not found") }
        val existing = sessionRepository.findByMatchIdOrderBySessionNumberAsc(matchId)
        if (existing.isEmpty() && match.status == MatchStatus.ACCEPTED) {
            generateSessions(match, null)
        }
    }

    override fun findSuggestions(userId: Long): List<MatchSummaryResult> {
        val viewer = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("User $userId not found") }

        val allMyMatches = matchRepository.findByInitiatorIdOrReceiverId(userId, userId)

        // Solo excluir usuarios con match PENDING o ACCEPTED activo (no COMPLETED ni REJECTED)
        val activeMatchedIds = allMyMatches
            .filter { it.status == MatchStatus.PENDING || it.status == MatchStatus.ACCEPTED }
            .flatMap { listOfNotNull(it.initiator?.id, it.receiver?.id) }
            .toSet()

        // Usuarios con los que hay un match REJECTED (bloqueados permanentemente para sugerencias)
        val rejectedMatchedIds = allMyMatches
            .filter { it.status == MatchStatus.REJECTED }
            .flatMap { listOfNotNull(it.initiator?.id, it.receiver?.id) }
            .toSet()

        return userRepository.findAll()
            .filter { other ->
                val otherId = other.id ?: return@filter false
                if (otherId == userId) return@filter false
                if (otherId in activeMatchedIds) return@filter false
                if (otherId in rejectedMatchedIds) return@filter false
                // Respetar profileVisible: si está oculto, no aparece en sugerencias
                if (other.profileVisible == false) return@filter false
                true
            }
            .map { other -> Pair(other, calculateCompatibility(viewer, other)) }
            .filter { (_, score) -> score > 50.0 }
            .sortedByDescending { (_, score) -> score }
            .take(5)
            .map { (other, score) -> toSuggestionSummary(other, score) }
    }

    override fun findDetail(matchId: Long, viewerUserId: Long): MatchDetailResult {
        val match = matchRepository.findById(matchId).orElseThrow {
            NoSuchElementException("Match $matchId not found")
        }
        val other = if (match.initiator?.id == viewerUserId) match.receiver else match.initiator
        val otherId = other?.id ?: 0L

        val first = other?.firstName ?: ""
        val last = other?.lastName ?: ""
        val initials = "${first.firstOrNull() ?: ""}${last.firstOrNull() ?: ""}".uppercase()

        val offered = skillRepository.findByUserIdAndSkillRole(otherId, SkillRole.OFFERED).map { it.name }
        val sought = skillRepository.findByUserIdAndSkillRole(otherId, SkillRole.SOUGHT).map { it.name }

        // Convert ISO-8601 dayOfWeek (1=Mon … 7=Sun) → 0-indexed (0=Mon … 6=Sun)
        val availability = availabilityRepository.findByUserId(otherId)
            .map { it.dayOfWeek - 1 }
            .distinct()
            .sorted()

        val sessionsCompleted = other?.totalSessions ?: 0

        val viewerOffered = skillRepository.findByUserIdAndSkillRole(viewerUserId, SkillRole.OFFERED)
            .map { it.name.lowercase() }.toSet()
        val viewerSought = skillRepository.findByUserIdAndSkillRole(viewerUserId, SkillRole.SOUGHT)
            .map { it.name.lowercase() }.toSet()
        val otherOfferedLower = offered.map { it.lowercase() }.toSet()
        val otherSoughtLower = sought.map { it.lowercase() }.toSet()

        val canLearn = otherOfferedLower.intersect(viewerSought)
        val canTeach = viewerOffered.intersect(otherSoughtLower)

        val aiAnalysis = buildString {
            append("$first y tú tienen una compatibilidad del ${match.compatibilityScore?.toInt() ?: 0}%. ")
            if (canLearn.isNotEmpty()) {
                append("Puedes aprender ${canLearn.joinToString(", ")} de $first. ")
            }
            if (canTeach.isNotEmpty()) {
                append("Tú puedes enseñarle ${canTeach.joinToString(", ")}.")
            }
            if (canLearn.isEmpty() && canTeach.isEmpty()) {
                append("Sus habilidades son complementarias y pueden aprender mutuamente.")
            }
        }

        return MatchDetailResult(
            matchId = match.id ?: 0L,
            otherUserId = otherId,
            otherUserName = "$first $last".trim(),
            otherUserInitials = initials,
            otherUserAvatarUrl = other?.avatarUrl,
            bio = other?.bio ?: "",
            rating = other?.rating ?: 0.0,
            sessionsCompleted = sessionsCompleted,
            compatibilityScore = match.compatibilityScore ?: 0.0,
            offeredSkills = offered,
            soughtSkills = sought,
            availability = availability,
            aiAnalysis = aiAnalysis,
            status = match.status.name,
            initiatorSkill = match.initiatorSkill,
            receiverSkill = match.receiverSkill,
            isReceiver = match.receiver?.id == viewerUserId,
            canLearnFromOther = offered.filter { it.lowercase() in canLearn },
            otherUserPhone = if (match.status == MatchStatus.ACCEPTED) other?.phone else null,
        )
    }

    private fun toDto(m: Match) = MatchResult(
        id = m.id,
        initiatorId = m.initiator?.id,
        receiverId = m.receiver?.id,
        compatibilityScore = m.compatibilityScore,
        status = m.status.name,
        createdAt = m.createdAt?.time,
    )

    /**
     * Compatibilidad combinada:
     *   70% skills  — habilidades que cada uno puede enseñar al otro
     *   30% horario — similitud de Jaccard sobre días disponibles
     */
    private fun calculateCompatibility(a: User, b: User): Double {
        val aId = a.id ?: return 0.0
        val bId = b.id ?: return 0.0

        val aOffers = skillRepository.findByUserIdAndSkillRole(aId, SkillRole.OFFERED).map { it.name.lowercase() }.toSet()
        val aSeeks  = skillRepository.findByUserIdAndSkillRole(aId, SkillRole.SOUGHT).map  { it.name.lowercase() }.toSet()
        val bOffers = skillRepository.findByUserIdAndSkillRole(bId, SkillRole.OFFERED).map { it.name.lowercase() }.toSet()
        val bSeeks  = skillRepository.findByUserIdAndSkillRole(bId, SkillRole.SOUGHT).map  { it.name.lowercase() }.toSet()

        val canLearn = bOffers.intersect(aSeeks).size   // lo que a aprende de b
        val canTeach = aOffers.intersect(bSeeks).size   // lo que a enseña a b
        val maxSkills = maxOf(aSeeks.size + bSeeks.size, 1)
        val skillScore = (canLearn + canTeach).toDouble() / maxSkills

        // Jaccard: |A ∩ B| / |A ∪ B|
        val aDays = availabilityRepository.findByUserId(aId).map { it.dayOfWeek }.toSet()
        val bDays = availabilityRepository.findByUserId(bId).map { it.dayOfWeek }.toSet()
        val union = (aDays + bDays).size
        val availScore = if (union == 0) 0.0 else aDays.intersect(bDays).size.toDouble() / union

        return (0.7 * skillScore + 0.3 * availScore) * 100.0
    }

    private fun generateSessions(match: Match, timezone: String?) {
        val initiator = match.initiator ?: return
        val receiver = match.receiver ?: return
        val initiatorId = initiator.id ?: return
        val receiverId = receiver.id ?: return

        val allInitiatorSkills = skillRepository.findByUserIdAndSkillRole(initiatorId, SkillRole.OFFERED)
        val allReceiverSkills = skillRepository.findByUserIdAndSkillRole(receiverId, SkillRole.OFFERED)

        // If the match was created with specific skills, filter to only those skills for the AI plan
        val initiatorSkills = if (match.initiatorSkill != null) {
            allInitiatorSkills.filter { it.name.lowercase() == match.initiatorSkill!!.lowercase() }
                .ifEmpty { allInitiatorSkills }
        } else {
            allInitiatorSkills
        }
        val receiverSkills = if (match.receiverSkill != null) {
            allReceiverSkills.filter { it.name.lowercase() == match.receiverSkill!!.lowercase() }
                .ifEmpty { allReceiverSkills }
        } else {
            allReceiverSkills
        }

        val allSkills = initiatorSkills + receiverSkills

        // Días en que ambos coinciden (ISO 1=Lun … 7=Dom). Si no hay, queda vacío
        // y las sesiones se crean sin fecha (scheduledAt = null), editables luego.
        val overlapDays = computeOverlapDays(initiatorId, receiverId)

        val effectiveDays = if (overlapDays.isNotEmpty()) overlapDays
            else availabilityRepository.findByUserId(initiatorId).map { it.dayOfWeek }.distinct().sorted()

        val availableDays = effectiveDays.map { dayName(it) }

        val aiPlans = aiServiceClient.generatePlan(
            AiPlanRequest(
                userAName = initiator.firstName ?: "Usuario A",
                userAOffers = initiatorSkills.map { it.name },
                userBName = receiver.firstName ?: "Usuario B",
                userBOffers = receiverSkills.map { it.name },
                availableDays = availableDays,
                overlapDayNumbers = effectiveDays,
                timezone = timezone
            )
        )

        if (aiPlans != null) {
            aiPlans.forEach { plan ->
                val correctPool = if (plan.sessionNumber % 2 == 1) receiverSkills else initiatorSkills
                val skill = allSkills.firstOrNull {
                    it.name.lowercase() == plan.skill.lowercase()
                } ?: correctPool.firstOrNull() ?: allSkills.firstOrNull() ?: return@forEach

                // Usar fecha sugerida por la IA si viene; sino null (el usuario la pone luego)
                val scheduledAt = plan.scheduledDate?.let { parseAiDate(it) }

                sessionRepository.save(Session(
                    match = match,
                    skill = skill,
                    sessionNumber = plan.sessionNumber,
                    title = plan.title,
                    description = plan.description,
                    status = SessionStatus.SCHEDULED,
                    scheduledAt = scheduledAt,
                    createdAt = Date(),
                ))
            }
        } else {
            // Fallback sin fecha — el usuario la define luego
            val fallbackTitles = listOf(
                "Sesión introductoria", "Práctica básica", "Ejercicios intermedios",
                "Sesión avanzada", "Técnicas aplicadas", "Revisión final",
            )
            fallbackTitles.forEachIndexed { index, title ->
                val skill = if (index % 2 == 0)
                    initiatorSkills.firstOrNull() ?: receiverSkills.firstOrNull()
                else
                    receiverSkills.firstOrNull() ?: initiatorSkills.firstOrNull()
                ?: return@forEachIndexed

                sessionRepository.save(Session(
                    match = match,
                    skill = skill,
                    sessionNumber = index + 1,
                    title = title,
                    status = SessionStatus.SCHEDULED,
                    scheduledAt = null,
                    createdAt = Date(),
                ))
            }
        }
    }

    private fun parseAiDate(dateStr: String): Date? = try {
        // Formato esperado: "Lun 9 jun" — parsear a Date
        val months = mapOf(
            "ene" to 1, "feb" to 2, "mar" to 3, "abr" to 4, "may" to 5, "jun" to 6,
            "jul" to 7, "ago" to 8, "sep" to 9, "oct" to 10, "nov" to 11, "dic" to 12
        )
        val parts = dateStr.trim().split("\\s+".toRegex())
        if (parts.size >= 3) {
            val day = parts[1].toInt()
            val month = months[parts[2].lowercase()] ?: return null
            val cal = java.util.Calendar.getInstance()
            cal.set(cal.get(java.util.Calendar.YEAR), month - 1, day, 9, 0, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            cal.time
        } else null
    } catch (_: Exception) { null }

    private fun dayName(dayOfWeek: Int): String = listOf(
        "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
    ).getOrElse(dayOfWeek - 1) { "Día $dayOfWeek" }

    /** Días (ISO 1=Lun … 7=Dom) en que ambos usuarios tienen disponibilidad. */
    private fun computeOverlapDays(initiatorId: Long, receiverId: Long): List<Int> {
        val a = availabilityRepository.findByUserId(initiatorId).map { it.dayOfWeek }.toSet()
        val b = availabilityRepository.findByUserId(receiverId).map { it.dayOfWeek }.toSet()
        return a.intersect(b).sorted()
    }

    /**
     * Genera [count] fechas futuras cayendo en los [overlapDays] (ISO 1=Lun … 7=Dom),
     * en orden cronológico. Si no hay días en común, devuelve una lista de nulls.
     */
    private fun computeScheduledDates(overlapDays: List<Int>, count: Int): List<Date?> {
        if (overlapDays.isEmpty()) return List(count) { null }
        val overlap = overlapDays.toSet()
        val cal = java.util.Calendar.getInstance().apply {
            add(java.util.Calendar.DAY_OF_MONTH, 1) // empezar mañana
            set(java.util.Calendar.HOUR_OF_DAY, 15)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val result = mutableListOf<Date?>()
        var guard = 0
        while (result.size < count && guard < count * 14) {
            val isoDay = ((cal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7) + 1 // Cal(1=Dom) → ISO(1=Lun)
            if (isoDay in overlap) result.add(cal.time)
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
            guard++
        }
        while (result.size < count) result.add(null)
        return result
    }

    private fun toSuggestionSummary(user: User, score: Double): MatchSummaryResult {
        val uid = user.id ?: 0L
        val offered = skillRepository.findByUserIdAndSkillRole(uid, SkillRole.OFFERED).map { it.name }
        val sought = skillRepository.findByUserIdAndSkillRole(uid, SkillRole.SOUGHT).map { it.name }
        val first = user.firstName ?: ""
        val last = user.lastName ?: ""
        val initials = "${first.firstOrNull() ?: ""}${last.firstOrNull() ?: ""}".uppercase()
        val availability = availabilityRepository.findByUserId(uid)
            .map { it.dayOfWeek - 1 }
            .distinct()
            .sorted()
        return MatchSummaryResult(
            matchId = 0L,
            otherUserId = uid,
            otherUserName = "$first $last".trim(),
            otherUserInitials = initials,
            otherUserAvatarUrl = user.avatarUrl,
            skillsOffered = offered,
            skillsSought = sought,
            otherUserRating = user.rating ?: 0.0,
            otherUserTotalSessions = user.totalSessions ?: 0,
            compatibilityScore = score,
            status = "SUGGESTION",
            sessionsCount = 0,
            isReceiver = false,
            availability = availability,
        )
    }

    private fun toSummary(match: Match, viewerUserId: Long): MatchSummaryResult {
        val other = if (match.initiator?.id == viewerUserId) match.receiver else match.initiator
        val otherId = other?.id ?: 0L

        val offered = skillRepository.findByUserIdAndSkillRole(otherId, SkillRole.OFFERED).map { it.name }
        val sought = skillRepository.findByUserIdAndSkillRole(otherId, SkillRole.SOUGHT).map { it.name }
        val sessionsCount = sessionRepository.findByMatchIdOrderBySessionNumberAsc(match.id ?: 0L).size

        val first = other?.firstName ?: ""
        val last = other?.lastName ?: ""
        val initials = "${first.firstOrNull() ?: ""}${last.firstOrNull() ?: ""}".uppercase()

        return MatchSummaryResult(
            matchId = match.id ?: 0L,
            otherUserId = otherId,
            otherUserName = "$first $last".trim(),
            otherUserInitials = initials,
            otherUserAvatarUrl = other?.avatarUrl,
            skillsOffered = offered,
            skillsSought = sought,
            otherUserRating = other?.rating ?: 0.0,
            otherUserTotalSessions = other?.totalSessions ?: 0,
            compatibilityScore = match.compatibilityScore ?: 0.0,
            status = match.status.name,
            sessionsCount = sessionsCount,
            isReceiver = match.receiver?.id == viewerUserId,
            initiatorSkill = match.initiatorSkill,
            receiverSkill = match.receiverSkill,
        )
    }

    /**
     * Rellena `initiatorSkill` / `receiverSkill` del match si alguno falta, para que
     * el intercambio quede acotado a exactamente dos skills:
     *  - initiatorSkill = lo que el iniciador enseña (= lo que aprende el receptor).
     *  - receiverSkill  = lo que el receptor enseña  (= lo que aprende el iniciador).
     * Prioriza la intersección con lo que el otro busca; si no hay, usa la primera
     * skill ofrecida por quien enseña.
     */
    private fun ensureExchangeSkills(match: Match) {
        val initiatorId = match.initiator?.id ?: return
        val receiverId = match.receiver?.id ?: return

        if (match.initiatorSkill == null) {
            match.initiatorSkill = pickExchangeSkill(teacherId = initiatorId, learnerId = receiverId)
        }
        if (match.receiverSkill == null) {
            match.receiverSkill = pickExchangeSkill(teacherId = receiverId, learnerId = initiatorId)
        }
    }

    /** Elige la skill que `teacher` enseña: la primera que el `learner` busca, o la primera ofrecida. */
    private fun pickExchangeSkill(teacherId: Long, learnerId: Long): String? {
        val teacherOffered = skillRepository.findByUserIdAndSkillRole(teacherId, SkillRole.OFFERED).map { it.name }
        val learnerSought = skillRepository.findByUserIdAndSkillRole(learnerId, SkillRole.SOUGHT)
            .map { it.name.lowercase() }.toSet()
        return teacherOffered.firstOrNull { it.lowercase() in learnerSought } ?: teacherOffered.firstOrNull()
    }
}
