package com.dexter.app.domain.model

/**
 * Full match representation with all the info the Match Detail screen needs.
 *
 * Composes data that in the backend comes from joining Match + User +
 * Skills + Availability. Kept in the domain layer because it represents
 * a business concept (match between two users with all their compatibility
 * context).
 */
data class MatchDetail(
    val matchId: Long,
    val otherUserId: Long,
    val initials: String,
    val name: String,
    val avatarUrl: String?,
    val bio: String,
    val rating: Double,
    val sessionsCompleted: Int,
    val compatibilityPercent: Int,
    val offeredSkills: List<String>,
    val soughtSkills: List<String>,
    val availability: List<Int>,
    val aiAnalysis: String,
    val status: MatchStatus
)
