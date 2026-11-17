package com.dexter.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class MatchSummaryResult(
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
    @JsonProperty("isReceiver")
    val isReceiver: Boolean,
    val availability: List<Int> = emptyList(),
    /** Skill the initiator teaches (what the receiver learns). Null until chosen. */
    val initiatorSkill: String? = null,
    /** Skill the receiver teaches (what the initiator learns). Null until chosen. */
    val receiverSkill: String? = null,
)
