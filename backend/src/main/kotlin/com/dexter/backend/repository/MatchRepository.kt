package com.dexter.backend.repository

import com.dexter.backend.entity.Match
import com.dexter.backend.entity.MatchStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MatchRepository : JpaRepository<Match, Long> {
    fun findByInitiatorIdOrReceiverId(initiatorId: Long, receiverId: Long): List<Match>
    fun findByInitiatorIdOrReceiverIdAndStatus(
        initiatorId: Long,
        receiverId: Long,
        status: MatchStatus,
    ): List<Match>
}
