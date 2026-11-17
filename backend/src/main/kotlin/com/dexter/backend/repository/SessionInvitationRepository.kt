package com.dexter.backend.repository

import com.dexter.backend.entity.InvitationStatus
import com.dexter.backend.entity.SessionInvitation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SessionInvitationRepository : JpaRepository<SessionInvitation, Long> {
    fun findByReceiverId(receiverId: Long): List<SessionInvitation>
    fun findByReceiverIdAndStatus(receiverId: Long, status: InvitationStatus): List<SessionInvitation>
}
