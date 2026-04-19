package com.dexter.backend.repository

import com.dexter.backend.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<Message, Long> {
    fun findByConversationIdOrderByCreatedAtAsc(conversationId: Long): List<Message>
}
