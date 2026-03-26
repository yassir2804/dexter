package com.dexter.app.domain.model

import java.util.Date

/**
 * Conversation between two users (chat container).
 *
 * @property id Conversation id.
 * @property user1Id First participant id.
 * @property user2Id Second participant id.
 * @property matchId Optional match id that originated the conversation.
 * @property lastMessage Optional last message text (cached).
 * @property lastMessageAt Optional timestamp of last message.
 * @property createdAt Creation timestamp.
 */
data class Conversation(
    val id: Long,
    val user1Id: Long,
    val user2Id: Long,
    val matchId: Long?,
    val lastMessage: String?,
    val lastMessageAt: Date?,
    val createdAt: Date
)
