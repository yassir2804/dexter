package com.dexter.app.data.datasource.model

/** DTO for Session. */
data class SessionDto(
    val id: Long? = null,
    val matchId: Long? = null,
    val skillId: Long? = null,
    val sessionNumber: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val scheduledAt: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long? = null
)
