package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.ConversationMapper
import com.dexter.app.data.mapper.MessageMapper
import com.dexter.app.domain.model.Conversation
import com.dexter.app.domain.model.Message
import com.dexter.app.domain.repository.ConversationRepository
import java.io.IOException

class ConversationRepositoryImpl(
    private val conversationMapper: ConversationMapper,
    private val messageMapper: MessageMapper
) : ConversationRepository {

    companion object { private const val TAG = "ConversationRepository" }

    override suspend fun getConversationsForUser(userId: Long): List<Conversation> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get conversations for user: $userId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getConversationsForUser", e)
        emptyList()
    }

    override suspend fun getMessages(conversationId: Long): List<Message> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get messages for conversation: $conversationId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getMessages", e)
        emptyList()
    }

    override suspend fun sendMessage(conversationId: Long, message: Message): Message = try {
        message
    } catch (e: IOException) {
        Log.e(TAG, "Failed to send message", e)
        message
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in sendMessage", e)
        message
    }
}
