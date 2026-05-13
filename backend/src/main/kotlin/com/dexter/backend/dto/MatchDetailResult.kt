package com.dexter.backend.dto

data class MatchDetailResult(
    val matchId: Long,
    val otherUserId: Long,
    val otherUserName: String,
    val otherUserInitials: String,
    val otherUserAvatarUrl: String?,
    val bio: String,
    val rating: Double,
    val sessionsCompleted: Int,
    val compatibilityScore: Double,
    val offeredSkills: List<String>,
    val soughtSkills: List<String>,
    /** 0 = Monday … 6 = Sunday (matches frontend chip index). */
    val availability: List<Int>,
    val aiAnalysis: String,
    val status: String,
)
