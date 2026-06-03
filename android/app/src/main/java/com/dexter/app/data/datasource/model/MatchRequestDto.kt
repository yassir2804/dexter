package com.dexter.app.data.datasource.model

data class MatchRequestDto(
    val initiatorId: Long,
    val receiverId: Long,
    val initiatorSkill: String? = null,
    val receiverSkill: String? = null,
)
