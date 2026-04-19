package com.dexter.backend.repository

import com.dexter.backend.entity.Conversation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConversationRepository : JpaRepository<Conversation, Long> {
    fun findByUser1IdOrUser2IdOrderByLastMessageAtDesc(user1Id: Long, user2Id: Long): List<Conversation>
    fun findByMatchId(matchId: Long): Conversation?
}
