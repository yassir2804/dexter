package com.dexter.app.domain.repository

import com.dexter.app.domain.model.SessionInvitation

/** Repository contract for session invitations between users. */
interface SessionInvitationRepository {
    suspend fun getInvitationById(id: Long): Result<SessionInvitation>
    suspend fun getInvitationsForUser(userId: Long): Result<List<SessionInvitation>>
    suspend fun sendInvitation(invitation: SessionInvitation): Result<SessionInvitation>
    suspend fun respondInvitation(id: Long, accept: Boolean): Result<SessionInvitation>
    suspend fun updateInvitation(invitation: SessionInvitation): Result<SessionInvitation>
}
