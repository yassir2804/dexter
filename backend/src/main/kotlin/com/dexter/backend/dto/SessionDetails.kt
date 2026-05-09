package com.dexter.backend.dto

data class SessionDetails(
    val id: Long,
    val matchId: Long,
    val skillName: String?,
    val sessionNumber: Int,
    val title: String,
    val description: String?,
    val status: String,
    val scheduledAt: Long?,
    val completedAt: Long?,
)
