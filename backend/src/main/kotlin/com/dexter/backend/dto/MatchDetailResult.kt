package com.dexter.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

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
    /** Skill the initiator will teach the receiver. Null if not yet chosen by the receiver. */
    val initiatorSkill: String?,
    /** Skill the receiver will teach the initiator. Null if not yet chosen. */
    val receiverSkill: String?,
    /** True when the viewer is the receiver (B) of this match. */
    @get:JsonProperty("isReceiver")
    val isReceiver: Boolean,
    /** Intersection of other's offered skills and viewer's sought skills — used by frontend picker. */
    val canLearnFromOther: List<String>,
    /** Phone number of the other user — only exposed when match is ACCEPTED. */
    val otherUserPhone: String?,
)
