package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.MessageMapper
import com.dexter.app.domain.model.Message
import com.dexter.app.domain.repository.MessageRepository
import java.io.IOException

class MessageRepositoryImpl(
    private val messageMapper: MessageMapper
) : MessageRepository {

    companion object { private const val TAG = "MessageRepository" }

    override suspend fun getMessageById(id: Long): Message? = try {
        null
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get message by id: $id", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getMessageById", e)
        null
    }

    override suspend fun getMessagesForConversation(conversationId: Long): List<Message> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get messages for conversation: $conversationId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getMessagesForConversation", e)
        emptyList()
    }

    override suspend fun sendMessage(message: Message): Message = try {
        message
    } catch (e: IOException) {
        Log.e(TAG, "Failed to send message", e)
        message
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in sendMessage", e)
        message
    }
}
