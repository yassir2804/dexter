package com.dexter.app.domain.model

/**
 * Enriched match data ready for display in Home screen cards.
 * Maps 1:1 from the backend MatchSummaryDto.
 */
data class MatchSummary(
    val matchId: Long,
    val otherUserId: Long,
    val name: String,
    val initials: String,
    val avatarUrl: String?,
    val skillsOffered: List<String>,
    val skillsSought: List<String>,
    val rating: Double,
    val totalSessions: Int,
    val compatibilityScore: Double,
    val status: String,
    val sessionsCount: Int,
)
