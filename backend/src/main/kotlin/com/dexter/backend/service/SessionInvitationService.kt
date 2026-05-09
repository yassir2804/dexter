package com.dexter.backend.service

import com.dexter.backend.dto.SessionInvitationDetails
import com.dexter.backend.dto.SessionInvitationRequest

interface SessionInvitationService {
    fun findByReceiver(receiverId: Long): List<SessionInvitationDetails>
    fun findById(id: Long): SessionInvitationDetails
    fun create(request: SessionInvitationRequest): SessionInvitationDetails
    fun accept(id: Long): SessionInvitationDetails
    fun reject(id: Long): SessionInvitationDetails
    fun cancel(id: Long): SessionInvitationDetails
}
