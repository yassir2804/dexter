package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.SessionInvitationMapper
import com.dexter.app.domain.model.SessionInvitation
import com.dexter.app.domain.repository.SessionInvitationRepository
import java.io.IOException

class SessionInvitationRepositoryImpl(
    private val sessionInvitationMapper: SessionInvitationMapper
) : SessionInvitationRepository {

    companion object { private const val TAG = "SessionInvitationRepository" }

    override suspend fun getInvitationById(id: Long): SessionInvitation? = try {
        null
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get invitation by id: $id", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getInvitationById", e)
        null
    }

    override suspend fun getInvitationsForUser(userId: Long): List<SessionInvitation> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get invitations for user: $userId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getInvitationsForUser", e)
        emptyList()
    }

    override suspend fun sendInvitation(invitation: SessionInvitation): SessionInvitation = try {
        invitation
    } catch (e: IOException) {
        Log.e(TAG, "Failed to send invitation", e)
        invitation
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in sendInvitation", e)
        invitation
    }

    override suspend fun updateInvitation(invitation: SessionInvitation): SessionInvitation = try {
        invitation
    } catch (e: IOException) {
        Log.e(TAG, "Failed to update invitation", e)
        invitation
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in updateInvitation", e)
        invitation
    }
}
