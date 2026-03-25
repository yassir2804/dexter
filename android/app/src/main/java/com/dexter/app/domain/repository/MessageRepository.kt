package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Message

/** Repository contract for messaging.
 * Implementations may use websockets, REST, or local storage.
 */
interface MessageRepository {
    suspend fun getMessageById(id: Long): Message?
    suspend fun getMessagesForConversation(conversationId: Long): List<Message>
    suspend fun sendMessage(message: Message): Message
}
