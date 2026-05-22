package com.dexter.app.data.datasource.model

data class MatchDetailDto(
    val matchId: Long = 0L,
    val otherUserId: Long = 0L,
    val otherUserName: String? = null,
    val otherUserInitials: String? = null,
    val otherUserAvatarUrl: String? = null,
    val bio: String? = null,
    val rating: Double = 0.0,
    val sessionsCompleted: Int = 0,
    val compatibilityScore: Double = 0.0,
    val offeredSkills: List<String> = emptyList(),
    val soughtSkills: List<String> = emptyList(),
    val availability: List<Int> = emptyList(),
    val aiAnalysis: String? = null,
    val status: String? = null,
    val initiatorSkill: String? = null,
    val isReceiver: Boolean = false,
    val canLearnFromOther: List<String> = emptyList(),
)
