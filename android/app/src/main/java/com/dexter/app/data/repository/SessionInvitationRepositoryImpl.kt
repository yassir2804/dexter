package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.datasource.SessionInvitationProvider
import com.dexter.app.data.mapper.SessionInvitationMapper
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.InvitationStatus
import com.dexter.app.domain.model.SessionInvitation
import com.dexter.app.domain.repository.SessionInvitationRepository
import java.io.IOException
import javax.inject.Inject

class SessionInvitationRepositoryImpl @Inject constructor(
    @Suppress("unused") private val sessionInvitationMapper: SessionInvitationMapper
) : SessionInvitationRepository {

    companion object { private const val TAG = "SessionInvitationRepository" }

    override suspend fun getInvitationById(id: Long): Result<SessionInvitation> = try {
        val invite = SessionInvitationProvider.findById(id)
            ?: return Result.failure(DomainError.UnknownError("Invitation $id not found"))
        Result.success(invite)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get invitation by id: $id", e)
        Result.failure(DomainError.NetworkError("Network error fetching invitation", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getInvitationById", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching invitation", e))
    }

    override suspend fun getInvitationsForUser(userId: Long): Result<List<SessionInvitation>> = try {
        Result.success(SessionInvitationProvider.findForUser(userId))
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get invitations for user: $userId", e)
        Result.failure(DomainError.NetworkError("Network error fetching invitations", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getInvitationsForUser", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching invitations", e))
    }

    override suspend fun sendInvitation(invitation: SessionInvitation): Result<SessionInvitation> = try {
        Log.i(TAG, "Invitation sent (mock) to user ${invitation.receiverId}")
        Result.success(invitation)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to send invitation", e)
        Result.failure(DomainError.NetworkError("Network error sending invitation", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in sendInvitation", e)
        Result.failure(DomainError.UnknownError("Unknown error sending invitation", e))
    }

    override suspend fun respondInvitation(id: Long, accept: Boolean): Result<SessionInvitation> = try {
        val invite = SessionInvitationProvider.findById(id)
            ?: return Result.failure(DomainError.UnknownError("Invitation $id not found"))
        val newStatus = if (accept) InvitationStatus.ACCEPTED else InvitationStatus.REJECTED
        Result.success(invite.copy(status = newStatus))
    } catch (e: IOException) {
        Log.e(TAG, "Failed to respond invitation: $id", e)
        Result.failure(DomainError.NetworkError("Network error responding invitation", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in respondInvitation", e)
        Result.failure(DomainError.UnknownError("Unknown error responding invitation", e))
    }

    override suspend fun updateInvitation(invitation: SessionInvitation): Result<SessionInvitation> = try {
        Result.success(invitation)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to update invitation", e)
        Result.failure(DomainError.NetworkError("Network error updating invitation", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in updateInvitation", e)
        Result.failure(DomainError.UnknownError("Unknown error updating invitation", e))
    }
}
