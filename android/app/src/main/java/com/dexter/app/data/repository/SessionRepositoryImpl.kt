package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.datasource.SessionProvider
import com.dexter.app.data.mapper.SessionMapper
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.Session
import com.dexter.app.domain.model.SessionStatus
import com.dexter.app.domain.repository.SessionRepository
import java.io.IOException
import java.util.Date
import javax.inject.Inject

/**
 * Mock-backed implementation while the Mockoon API is not wired yet.
 *
 * Every method follows the try/IOException/Exception envelope used by
 * MatchRepositoryImpl / UserRepositoryImpl. Yazir only needs to replace
 * the body of each method with a `safeApiCall { remoteDataSource.xxx() }`
 * fallback to [SessionProvider] — no contract changes required.
 */
class SessionRepositoryImpl @Inject constructor(
    @Suppress("unused") private val sessionMapper: SessionMapper
) : SessionRepository {

    companion object { private const val TAG = "SessionRepository" }

    override suspend fun getSessionById(id: Long): Result<Session> = try {
        val session = SessionProvider.findSessionById(id)
            ?: return Result.failure(DomainError.UnknownError("Session $id not found"))
        Result.success(session)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get session by id: $id", e)
        Result.failure(DomainError.NetworkError("Network error fetching session $id", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getSessionById", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching session $id", e))
    }

    override suspend fun getSessionsByMatch(matchId: Long): Result<List<Session>> = try {
        Result.success(SessionProvider.findSessionsByMatch(matchId))
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get sessions for match: $matchId", e)
        Result.failure(DomainError.NetworkError("Network error fetching sessions", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getSessionsByMatch", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching sessions", e))
    }

    override suspend fun getActiveSession(sessionId: Long): Result<Session> = try {
        Result.success(SessionProvider.findActiveSession(sessionId))
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get active session: $sessionId", e)
        Result.failure(DomainError.NetworkError("Network error fetching active session", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getActiveSession", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching active session", e))
    }

    override suspend fun createSession(session: Session): Result<Session> = try {
        Result.success(session)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to create session", e)
        Result.failure(DomainError.NetworkError("Network error creating session", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in createSession", e)
        Result.failure(DomainError.UnknownError("Unknown error creating session", e))
    }

    override suspend fun updateSession(session: Session): Result<Session> = try {
        // Mock echoes back the updated session. Yazir replaces with PUT call.
        Result.success(session)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to update session", e)
        Result.failure(DomainError.NetworkError("Network error updating session", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in updateSession", e)
        Result.failure(DomainError.UnknownError("Unknown error updating session", e))
    }

    override suspend fun cancelSession(sessionId: Long): Result<Unit> = try {
        Log.i(TAG, "Session $sessionId cancelled (mock, no persistence)")
        Result.success(Unit)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to cancel session: $sessionId", e)
        Result.failure(DomainError.NetworkError("Network error cancelling session", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in cancelSession", e)
        Result.failure(DomainError.UnknownError("Unknown error cancelling session", e))
    }

    override suspend fun completeSession(sessionId: Long, notes: String): Result<Session> = try {
        val base = SessionProvider.findSessionById(sessionId)
            ?: return Result.failure(DomainError.UnknownError("Session $sessionId not found"))
        val completed = base.copy(
            status = SessionStatus.COMPLETED,
            completedAt = Date()
        )
        Result.success(completed)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to complete session: $sessionId", e)
        Result.failure(DomainError.NetworkError("Network error completing session", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in completeSession", e)
        Result.failure(DomainError.UnknownError("Unknown error completing session", e))
    }
}
