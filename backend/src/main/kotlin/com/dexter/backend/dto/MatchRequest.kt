package com.dexter.backend.dto

data class MatchRequest(
    val initiatorId: Long,
    val receiverId: Long,
)
