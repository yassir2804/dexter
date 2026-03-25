package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Conversation
import com.dexter.app.domain.model.Message

/** Repository contract for conversations and messages. */
interface ConversationRepository {
    suspend fun getConversationsForUser(userId: Long): List<Conversation>
    suspend fun getMessages(conversationId: Long): List<Message>
    suspend fun sendMessage(conversationId: Long, message: Message): Message
}
