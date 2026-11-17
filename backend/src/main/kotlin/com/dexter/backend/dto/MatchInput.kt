package com.dexter.backend.dto

data class MatchInput(
    val initiatorId: Long,
    val receiverId: Long,
    val initiatorSkill: String? = null,
    val receiverSkill: String? = null,
)
