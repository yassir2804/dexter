package com.dexter.app.data.model

/** DTO for Conversation wire format. */
data class ConversationDto(
    val id: Long? = null,
    val user1Id: Long? = null,
    val user2Id: Long? = null,
    val matchId: Long? = null,
    val lastMessage: String? = null,
    val lastMessageAt: Long? = null,
    val createdAt: Long? = null
)
