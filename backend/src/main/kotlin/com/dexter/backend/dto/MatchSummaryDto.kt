package com.dexter.backend.dto

data class MatchSummaryDto(
    val matchId: Long,
    val otherUserId: Long,
    val otherUserName: String,
    val otherUserInitials: String,
    val otherUserAvatarUrl: String?,
    val skillsOffered: List<String>,
    val skillsSought: List<String>,
    val otherUserRating: Double,
    val otherUserTotalSessions: Int,
    val compatibilityScore: Double,
    val status: String,
    val sessionsCount: Int,
)
