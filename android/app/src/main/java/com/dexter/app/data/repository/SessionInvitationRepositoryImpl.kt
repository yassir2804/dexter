package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.SessionInvitationMapper
import com.dexter.app.data.remote.SessionInvitationRemoteDataSource
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.InvitationStatus
import com.dexter.app.domain.model.SessionInvitation
import com.dexter.app.domain.repository.SessionInvitationRepository
import java.io.IOException
import javax.inject.Inject

class SessionInvitationRepositoryImpl @Inject constructor(
    private val remoteDataSource: SessionInvitationRemoteDataSource,
    private val sessionInvitationMapper: SessionInvitationMapper
) : SessionInvitationRepository {

    companion object { private const val TAG = "SessionInvitationRepository" }

    override suspend fun getInvitationById(id: Long): Result<SessionInvitation> = try {
        remoteDataSource.getInvitations().fold(
            onSuccess = { dtos ->
                val dto = dtos.firstOrNull { it.id == id }
                if (dto != null) {
                    Result.success(sessionInvitationMapper.mapToDomain(dto))
                } else {
                    Result.failure(DomainError.UnknownError("Invitation $id not found"))
                }
            },
            onFailure = { exception ->
                Result.failure(DomainError.NetworkError("Network error fetching invitation", exception))
            }
        )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get invitation by id: $id", e)
        Result.failure(DomainError.NetworkError("Network error fetching invitation", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getInvitationById", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching invitation", e))
    }

    override suspend fun getInvitationsForUser(userId: Long): Result<List<SessionInvitation>> = try {
        remoteDataSource.getInvitations().map { dtos ->
            dtos
                .map { sessionInvitationMapper.mapToDomain(it) }
                .filter { it.senderId == userId || it.receiverId == userId }
        }
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get invitations for user: $userId", e)
        Result.failure(DomainError.NetworkError("Network error fetching invitations", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getInvitationsForUser", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching invitations", e))
    }

    override suspend fun sendInvitation(invitation: SessionInvitation): Result<SessionInvitation> = try {
        // No send endpoint in current Mockoon invitation block yet.
        Log.i(TAG, "Invitation send simulated locally for user ${invitation.receiverId}")
        Result.success(invitation)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to send invitation", e)
        Result.failure(DomainError.NetworkError("Network error sending invitation", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in sendInvitation", e)
        Result.failure(DomainError.UnknownError("Unknown error sending invitation", e))
    }

    override suspend fun respondInvitation(id: Long, accept: Boolean): Result<SessionInvitation> = try {
        getInvitationById(id).map { invite ->
            val newStatus = if (accept) InvitationStatus.ACCEPTED else InvitationStatus.REJECTED
            invite.copy(status = newStatus)
        }
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
