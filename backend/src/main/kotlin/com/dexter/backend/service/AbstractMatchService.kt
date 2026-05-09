package com.dexter.backend.service

import com.dexter.backend.dto.MatchRequest
import com.dexter.backend.dto.MatchSummaryDto
import com.dexter.backend.entity.Match
import com.dexter.backend.entity.MatchStatus
import com.dexter.backend.entity.Notification
import com.dexter.backend.entity.NotificationType
import com.dexter.backend.entity.Session
import com.dexter.backend.entity.SessionStatus
import com.dexter.backend.entity.SkillRole
import com.dexter.backend.entity.User
import com.dexter.backend.repository.MatchRepository
import com.dexter.backend.repository.NotificationRepository
import com.dexter.backend.repository.SessionRepository
import com.dexter.backend.repository.SkillRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AbstractMatchService(
    @Autowired val matchRepository: MatchRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val skillRepository: SkillRepository,
    @Autowired val sessionRepository: SessionRepository,
    @Autowired val notificationRepository: NotificationRepository,
) : MatchService {

    override fun findByUser(userId: Long): List<MatchSummaryDto> =
        matchRepository.findByInitiatorIdOrReceiverId(userId, userId)
            .map { toSummary(it, userId) }

    override fun create(request: MatchRequest): MatchSummaryDto {
        val initiator = userRepository.findById(request.initiatorId)
            .orElseThrow { NoSuchElementException("User ${request.initiatorId} not found") }
        val receiver = userRepository.findById(request.receiverId)
            .orElseThrow { NoSuchElementException("User ${request.receiverId} not found") }

        val score = calculateCompatibility(initiator, receiver)

        val match = matchRepository.save(Match(
            initiator = initiator,
            receiver = receiver,
            compatibilityScore = score,
            status = MatchStatus.PENDING,
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

    override fun accept(id: Long, viewerUserId: Long): MatchSummaryDto {
        val match = matchRepository.findById(id)
            .orElseThrow { NoSuchElementException("Match $id not found") }

        match.status = MatchStatus.ACCEPTED
        val saved = matchRepository.save(match)

        generateSessions(saved)

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
        match.status = MatchStatus.REJECTED
        matchRepository.save(match)
    }

    private fun calculateCompatibility(a: User, b: User): Double {
        val aId = a.id ?: return 0.0
        val bId = b.id ?: return 0.0

        val aOffers = skillRepository.findByUserIdAndSkillRole(aId, SkillRole.OFFERED)
            .map { it.name.lowercase() }.toSet()
        val aSeeks = skillRepository.findByUserIdAndSkillRole(aId, SkillRole.SOUGHT)
            .map { it.name.lowercase() }.toSet()
        val bOffers = skillRepository.findByUserIdAndSkillRole(bId, SkillRole.OFFERED)
            .map { it.name.lowercase() }.toSet()
        val bSeeks = skillRepository.findByUserIdAndSkillRole(bId, SkillRole.SOUGHT)
            .map { it.name.lowercase() }.toSet()

        val aTeachesB = aOffers.intersect(bSeeks).size
        val bTeachesA = bOffers.intersect(aSeeks).size
        val maxPossible = maxOf(aSeeks.size + bSeeks.size, 1)

        return ((aTeachesB + bTeachesA).toDouble() / maxPossible * 100.0).coerceIn(0.0, 100.0)
    }

    private fun generateSessions(match: Match) {
        val initiatorId = match.initiator?.id ?: return
        val receiverId = match.receiver?.id ?: return

        val initiatorSkills = skillRepository.findByUserIdAndSkillRole(initiatorId, SkillRole.OFFERED)
        val receiverSkills = skillRepository.findByUserIdAndSkillRole(receiverId, SkillRole.OFFERED)

        val titles = listOf(
            "Sesión introductoria",
            "Práctica básica",
            "Ejercicios intermedios",
            "Sesión avanzada",
            "Revisión final",
        )

        titles.forEachIndexed { index, title ->
            val skill = if (index % 2 == 0) {
                initiatorSkills.firstOrNull() ?: receiverSkills.firstOrNull()
            } else {
                receiverSkills.firstOrNull() ?: initiatorSkills.firstOrNull()
            } ?: return@forEachIndexed

            sessionRepository.save(Session(
                match = match,
                skill = skill,
                sessionNumber = index + 1,
                title = title,
                status = SessionStatus.SCHEDULED,
                createdAt = Date(),
            ))
        }
    }

    private fun toSummary(match: Match, viewerUserId: Long): MatchSummaryDto {
        val other = if (match.initiator?.id == viewerUserId) match.receiver else match.initiator
        val otherId = other?.id ?: 0L

        val offered = skillRepository.findByUserIdAndSkillRole(otherId, SkillRole.OFFERED).map { it.name }
        val sought = skillRepository.findByUserIdAndSkillRole(otherId, SkillRole.SOUGHT).map { it.name }
        val sessionsCount = sessionRepository.findByMatchIdOrderBySessionNumberAsc(match.id ?: 0L).size

        val first = other?.firstName ?: ""
        val last = other?.lastName ?: ""
        val initials = "${first.firstOrNull() ?: ""}${last.firstOrNull() ?: ""}".uppercase()

        return MatchSummaryDto(
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
        )
    }
}
