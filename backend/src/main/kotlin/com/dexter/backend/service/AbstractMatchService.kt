package com.dexter.backend.service

import com.dexter.backend.dto.MatchDto
import com.dexter.backend.dto.MatchSummaryDto
import com.dexter.backend.entity.Match
import com.dexter.backend.entity.MatchStatus
import com.dexter.backend.entity.SkillRole
import com.dexter.backend.repository.MatchRepository
import com.dexter.backend.repository.SkillRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AbstractMatchService(
    @Autowired val matchRepository: MatchRepository,
    @Autowired val skillRepository: SkillRepository,
) : MatchService {

    override fun findByUserId(userId: Long): List<MatchDto> =
        matchRepository.findByInitiatorIdOrReceiverId(userId, userId)
            .map(::toDto)

    override fun findByUserIdAndStatus(userId: Long, status: MatchStatus): List<MatchDto> =
        matchRepository.findByInitiatorIdOrReceiverIdAndStatus(userId, userId, status)
            .map(::toDto)

    override fun findById(matchId: Long): MatchDto {
        val match = matchRepository.findById(matchId).orElseThrow {
            NoSuchElementException("Match $matchId not found")
        }
        return toDto(match)
    }

    override fun findSummariesByUserId(userId: Long): List<MatchSummaryDto> =
        matchRepository.findByInitiatorIdOrReceiverId(userId, userId)
            .map { toSummary(it, userId) }

    private fun toDto(m: Match) = MatchDto(
        id = m.id,
        initiatorId = m.initiator?.id,
        receiverId = m.receiver?.id,
        compatibilityScore = m.compatibilityScore,
        status = m.status.name,
        createdAt = m.createdAt?.time,
    )

    private fun toSummary(m: Match, currentUserId: Long): MatchSummaryDto {
        val other = if (m.initiator?.id == currentUserId) m.receiver!! else m.initiator!!
        val otherSkills = skillRepository.findByUserId(other.id!!)
        val offers = otherSkills.filter { it.skillRole == SkillRole.OFFERED }
            .joinToString(", ") { it.name }
        val seeks = otherSkills.filter { it.skillRole == SkillRole.SOUGHT }
            .joinToString(", ") { it.name }

        val compatPct = m.compatibilityScore?.let { "${(it * 100).toInt()}%" } ?: "—"
        val ratingStr = other.rating?.let { "%.1f".format(it) } ?: "—"

        return MatchSummaryDto(
            initials = "${other.firstName?.firstOrNull() ?: ""}${other.lastName?.firstOrNull() ?: ""}".uppercase(),
            name = "${other.firstName ?: ""} ${other.lastName ?: ""}".trim(),
            avatarUrl = other.avatarUrl,
            offers = offers,
            seeks = seeks,
            rating = ratingStr,
            sessions = other.totalSessions ?: 0,
            compatibilityPercent = compatPct,
        )
    }
}
