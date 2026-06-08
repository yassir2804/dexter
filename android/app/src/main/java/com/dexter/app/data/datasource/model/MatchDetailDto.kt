package com.dexter.app.data.datasource.model

import com.google.gson.annotations.SerializedName

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
    val receiverSkill: String? = null,
    @SerializedName("isReceiver") val receiver: Boolean = false,
    val canLearnFromOther: List<String> = emptyList(),
    val otherUserPhone: String? = null,
)
