package com.dexter.app.data.datasource.model

/** DTO for MatchSummary — mirrors the backend's MatchSummaryDto response. */
data class MatchSummaryDto(
    val matchId: Long = 0L,
    val otherUserId: Long = 0L,
    val otherUserName: String? = null,
    val otherUserInitials: String? = null,
    val otherUserAvatarUrl: String? = null,
    val skillsOffered: List<String>? = null,
    val skillsSought: List<String>? = null,
    val otherUserRating: Double = 0.0,
    val otherUserTotalSessions: Int = 0,
    val compatibilityScore: Double = 0.0,
    val status: String? = null,
    val sessionsCount: Int = 0,
    val isReceiver: Boolean = false,
    val availability: List<Int>? = null,
)
