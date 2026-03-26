package com.dexter.app.domain.repository

import com.dexter.app.domain.model.SessionInvitation

/** Repository contract for session invitations between users. */
interface SessionInvitationRepository {
    suspend fun getInvitationById(id: Long): SessionInvitation?
    suspend fun getInvitationsForUser(userId: Long): List<SessionInvitation>
    suspend fun sendInvitation(invitation: SessionInvitation): SessionInvitation
    suspend fun updateInvitation(invitation: SessionInvitation): SessionInvitation
}
