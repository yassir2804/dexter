package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.MatchSummaryDto
import com.dexter.app.domain.model.MatchSummary
import javax.inject.Inject

class MatchSummaryMapper @Inject constructor() {

    fun mapToDomain(dto: MatchSummaryDto): MatchSummary = MatchSummary(
        matchId = dto.matchId,
        otherUserId = dto.otherUserId,
        name = dto.otherUserName ?: "",
        initials = dto.otherUserInitials ?: "",
        avatarUrl = dto.otherUserAvatarUrl,
        skillsOffered = dto.skillsOffered ?: emptyList(),
        skillsSought = dto.skillsSought ?: emptyList(),
        rating = dto.otherUserRating,
        totalSessions = dto.otherUserTotalSessions,
        compatibilityScore = dto.compatibilityScore,
        status = dto.status ?: "PENDING",
        sessionsCount = dto.sessionsCount,
        isReceiver = dto.receiver,
        availability = dto.availability ?: emptyList(),
    )

    fun mapToDto(domain: MatchSummary): MatchSummaryDto = MatchSummaryDto(
        matchId = domain.matchId,
        otherUserId = domain.otherUserId,
        otherUserName = domain.name,
        otherUserInitials = domain.initials,
        otherUserAvatarUrl = domain.avatarUrl,
        skillsOffered = domain.skillsOffered,
        skillsSought = domain.skillsSought,
        otherUserRating = domain.rating,
        otherUserTotalSessions = domain.totalSessions,
        compatibilityScore = domain.compatibilityScore,
        status = domain.status,
        sessionsCount = domain.sessionsCount,
    )
}
