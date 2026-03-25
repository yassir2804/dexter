package com.dexter.app.data.model

/** DTO for Match. */
data class MatchDto(
    val id: Long? = null,
    val initiatorId: Long? = null,
    val receiverId: Long? = null,
    val compatibilityScore: Double? = null,
    val status: String? = null,
    val createdAt: Long? = null
)
