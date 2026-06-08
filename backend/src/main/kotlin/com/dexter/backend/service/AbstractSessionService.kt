package com.dexter.backend.service

import com.dexter.backend.client.AiServiceClient
import com.dexter.backend.dto.SessionDetails
import com.dexter.backend.dto.SessionInput
import com.dexter.backend.entity.MatchStatus
import com.dexter.backend.entity.Notification
import com.dexter.backend.entity.NotificationType
import com.dexter.backend.entity.Session
import com.dexter.backend.entity.SessionStatus
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
class AbstractSessionService(
    @Autowired val sessionRepository: SessionRepository,
    @Autowired val matchRepository: MatchRepository,
    @Autowired val skillRepository: SkillRepository,
    @Autowired val notificationRepository: NotificationRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val availabilityRepository: AvailabilityRepository,
    @Autowired val aiServiceClient: AiServiceClient,
) : SessionService {

    override fun findByMatch(matchId: Long): List<SessionDetails> =
        sessionRepository.findByMatchIdOrderBySessionNumberAsc(matchId).map(::toDetails)

    override fun findById(id: Long): SessionDetails =
        toDetails(sessionRepository.findById(id).orElseThrow {
            NoSuchElementException("Session with id $id not found")
        })

    override fun create(request: SessionInput): SessionDetails {
        val matchId = request.matchId ?: throw IllegalArgumentException("matchId is required")
        val skillId = request.skillId ?: throw IllegalArgumentException("skillId is required")
        val sessionNumber = request.sessionNumber
            ?: throw IllegalArgumentException("sessionNumber is required")
        val title = request.title ?: throw IllegalArgumentException("title is required")

        val match = matchRepository.findById(matchId).orElseThrow {
            NoSuchElementException("Match with id $matchId not found")
        }
        val skill = skillRepository.findById(skillId).orElseThrow {
            NoSuchElementException("Skill with id $skillId not found")
        }
        val status = parseStatus(request.status) ?: SessionStatus.SCHEDULED

        val saved = sessionRepository.save(
            Session(
                match = match,
                skill = skill,
                sessionNumber = sessionNumber,
                title = title,
                description = request.description,
                status = status,
                scheduledAt = request.scheduledAt,
                completedAt = request.completedAt,
                createdAt = Date(),
            )
        )
        return toDetails(saved)
    }

    @Transactional
    override fun update(id: Long, request: SessionInput): SessionDetails {
        val session = sessionRepository.findById(id).orElseThrow {
            NoSuchElementException("Session with id $id not found")
        }
        request.title?.let { session.title = it }
        request.description?.let { session.description = it }
        request.sessionNumber?.let { session.sessionNumber = it }
        request.scheduledAt?.let { session.scheduledAt = it }
        request.completedAt?.let { session.completedAt = it }
        parseStatus(request.status)?.let { session.status = it }
        request.skillId?.let { skillId ->
            session.skill = skillRepository.findById(skillId).orElseThrow {
                NoSuchElementException("Skill with id $skillId not found")
            }
        }
        val saved = sessionRepository.save(session)

        // Reordenar todas las sesiones del match por fecha para que los números sean consistentes
        val matchId = saved.match?.id
        if (matchId != null && saved.scheduledAt != null) {
            val allSessions = sessionRepository.findByMatchIdOrderBySessionNumberAsc(matchId)
                .filter { it.status != SessionStatus.CANCELLED }
                .sortedWith(compareBy({ it.scheduledAt ?: Date(Long.MAX_VALUE) }, { it.sessionNumber }))
            allSessions.forEachIndexed { index, s ->
                if (s.sessionNumber != index + 1) {
                    s.sessionNumber = index + 1
                    sessionRepository.save(s)
                }
            }
        }

        return toDetails(sessionRepository.findById(id).orElse(saved))
    }

    @Transactional
    override fun complete(id: Long): SessionDetails {
        val session = sessionRepository.findById(id).orElseThrow {
            NoSuchElementException("Session with id $id not found")
        }
        // Idempotente: si ya estaba COMPLETED no re-disparamos efectos secundarios.
        val wasAlreadyCompleted = session.status == SessionStatus.COMPLETED
        session.status = SessionStatus.COMPLETED
        session.completedAt = session.completedAt ?: Date()
        val saved = sessionRepository.save(session)

        if (!wasAlreadyCompleted) {
            incrementTotalSessions(saved)
            notifyStudentToReview(saved)
        }

        // Si todas las sesiones del match estan COMPLETED, cerrar el match.
        val match = saved.match
        if (match != null && match.status == MatchStatus.ACCEPTED) {
            val all = sessionRepository.findByMatchIdOrderBySessionNumberAsc(match.id ?: 0L)
            if (all.isNotEmpty() && all.all { it.status == SessionStatus.COMPLETED }) {
                match.status = MatchStatus.COMPLETED
                matchRepository.save(match)
                notifyMatchCompleted(match)
            }
        }

        return toDetails(saved)
    }

    /**
     * Incrementa `totalSessions` para los dos participantes del match
     * (no solo el profesor: ambos cuentan la sesion en su historial).
     */
    private fun incrementTotalSessions(session: Session) {
        val match = session.match ?: return
        listOfNotNull(match.initiator, match.receiver).forEach { u ->
            u.totalSessions = (u.totalSessions ?: 0) + 1
            u.updatedAt = Date()
            userRepository.save(u)
        }
    }

    /**
     * Manda una notificacion al "estudiante" de la sesion (el otro miembro
     * del match, el que no enseno) para que deje una resena al profesor.
     * Si por alguna razon no se puede determinar al profesor, notifica a
     * ambos para que se evaluen mutuamente.
     */
    private fun notifyStudentToReview(session: Session) {
        val match = session.match ?: return
        val teacher = session.skill?.user
        val initiator = match.initiator
        val receiver = match.receiver

        val recipients = when (teacher?.id) {
            initiator?.id -> listOfNotNull(receiver)
            receiver?.id -> listOfNotNull(initiator)
            else -> listOfNotNull(initiator, receiver)
        }

        recipients.forEach { student ->
            notificationRepository.save(Notification(
                user = student,
                referenceId = session.id,
                title = "Sesion completada",
                body = "Deja tu resena a ${teacher?.firstName ?: "tu companero"} de la sesion '${session.title}'",
                notificationType = NotificationType.SESSION_REMINDER,
                isRead = false,
                createdAt = Date(),
            ))
        }
    }

    private fun notifyMatchCompleted(match: com.dexter.backend.entity.Match) {
        val initiator = match.initiator
        val receiver = match.receiver
        val title = "Match completado"
        val body = "Han terminado todas las sesiones del intercambio. Felicidades!"
        listOfNotNull(initiator, receiver).forEach { user ->
            notificationRepository.save(Notification(
                user = user,
                referenceId = match.id,
                title = title,
                body = body,
                notificationType = NotificationType.SESSION_REMINDER,
                isRead = false,
                createdAt = Date(),
            ))
        }
    }

    @Transactional
    override fun delete(id: Long) {
        val session = sessionRepository.findById(id).orElseThrow {
            NoSuchElementException("Session with id $id not found")
        }
        val matchId = session.match?.id ?: run {
            sessionRepository.deleteById(id)
            return
        }
        sessionRepository.deleteById(id)
        // Renumerar las sesiones restantes en orden cronológico
        val remaining = sessionRepository.findByMatchIdOrderBySessionNumberAsc(matchId)
            .filter { it.status != SessionStatus.CANCELLED }
            .sortedWith(compareBy({ it.scheduledAt ?: Date(Long.MAX_VALUE) }, { it.sessionNumber }))
        remaining.forEachIndexed { index, s ->
            if (s.sessionNumber != index + 1) {
                s.sessionNumber = index + 1
                sessionRepository.save(s)
            }
        }
    }

    private fun parseStatus(s: String?): SessionStatus? = s?.let {
        try { SessionStatus.valueOf(it) }
        catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid status: $it (expected SCHEDULED|COMPLETED|CANCELLED)")
        }
    }

    private fun toDetails(s: Session): SessionDetails = SessionDetails(
        id = s.id,
        matchId = s.match?.id,
        skillId = s.skill?.id,
        skillName = s.skill?.name,
        sessionNumber = s.sessionNumber,
        title = s.title,
        description = s.description,
        status = s.status.name,
        scheduledAt = s.scheduledAt?.time,
        completedAt = s.completedAt?.time,
        createdAt = s.createdAt?.time,
    )
}
