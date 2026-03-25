package com.dexter.app.data.model

/** DTO for SessionInvitation. */
data class SessionInvitationDto(
    val id: Long? = null,
    val senderId: Long? = null,
    val receiverId: Long? = null,
    val matchId: Long? = null,
    val status: String? = null,
    val expiresAt: Long? = null,
    val createdAt: Long? = null
)
