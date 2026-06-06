package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.SessionMapper
import com.dexter.app.data.remote.SessionRemoteDataSource
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.ScheduleSlot
import com.dexter.app.domain.model.Session
import com.dexter.app.domain.repository.SessionRepository
import java.io.IOException
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
    private val remoteDataSource: SessionRemoteDataSource,
    private val sessionMapper: SessionMapper
) : SessionRepository {

    companion object { private const val TAG = "SessionRepository" }

    override suspend fun getSessionById(id: Long): Result<Session> = try {
        remoteDataSource.getSessionById(id).fold(
            onSuccess = { dto -> Result.success(sessionMapper.mapToDomain(dto)) },
            onFailure = { exception ->
                Result.failure(DomainError.NetworkError("Network error fetching session $id", exception))
            }
        )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get session by id: $id", e)
        Result.failure(DomainError.NetworkError("Network error fetching session $id", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getSessionById", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching session $id", e))
    }

    override suspend fun getSessionsByMatch(matchId: Long): Result<List<Session>> = try {
        remoteDataSource.getSessions(matchId).fold(
            onSuccess = { dtos ->
                Result.success(dtos.map { sessionMapper.mapToDomain(it) })
            },
            onFailure = { exception ->
                Result.failure(DomainError.NetworkError("Network error fetching sessions", exception))
            }
        )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get sessions for match: $matchId", e)
        Result.failure(DomainError.NetworkError("Network error fetching sessions", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getSessionsByMatch", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching sessions", e))
    }

    override suspend fun getActiveSession(sessionId: Long): Result<Session> = try {
        getSessionById(sessionId)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get active session: $sessionId", e)
        Result.failure(DomainError.NetworkError("Network error fetching active session", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getActiveSession", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching active session", e))
    }

    override suspend fun createSession(session: Session): Result<Session> = try {
        // No create endpoint in current Mockoon sessions block yet.
        Result.success(session)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to create session", e)
        Result.failure(DomainError.NetworkError("Network error creating session", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in createSession", e)
        Result.failure(DomainError.UnknownError("Unknown error creating session", e))
    }

    override suspend fun updateSession(session: Session): Result<Session> = try {
        remoteDataSource.updateSession(session.id, sessionMapper.mapToDto(session)).fold(
            onSuccess = { dto -> Result.success(sessionMapper.mapToDomain(dto)) },
            onFailure = { exception ->
                Log.e(TAG, "Remote updateSession failed", exception)
                Result.failure(DomainError.NetworkError("Network error updating session", exception))
            }
        )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to update session", e)
        Result.failure(DomainError.NetworkError("Network error updating session", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in updateSession", e)
        Result.failure(DomainError.UnknownError("Unknown error updating session", e))
    }

    override suspend fun cancelSession(sessionId: Long): Result<Unit> = try {
        remoteDataSource.deleteSession(sessionId).fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { exception ->
                Log.e(TAG, "Remote cancelSession failed", exception)
                Result.failure(DomainError.NetworkError("Network error cancelling session", exception))
            }
        )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to cancel session: $sessionId", e)
        Result.failure(DomainError.NetworkError("Network error cancelling session", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in cancelSession", e)
        Result.failure(DomainError.UnknownError("Unknown error cancelling session", e))
    }

    override suspend fun completeSession(sessionId: Long, notes: String): Result<Session> = try {
        remoteDataSource.completeSession(sessionId).fold(
            onSuccess = { dto -> Result.success(sessionMapper.mapToDomain(dto)) },
            onFailure = { exception ->
                Log.e(TAG, "Remote completeSession failed", exception)
                Result.failure(DomainError.NetworkError("Network error completing session", exception))
            }
        )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to complete session: $sessionId", e)
        Result.failure(DomainError.NetworkError("Network error completing session", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in completeSession", e)
        Result.failure(DomainError.UnknownError("Unknown error completing session", e))
    }

    override suspend fun suggestSchedule(sessionId: Long): Result<List<ScheduleSlot>> = try {
        remoteDataSource.suggestSchedule(sessionId).fold(
            onSuccess = { dtos ->
                Result.success(dtos.mapNotNull { dto ->
                    if (dto.day != null && dto.time != null) ScheduleSlot(dto.day, dto.time) else null
                })
            },
            onFailure = { exception ->
                Log.e(TAG, "Remote suggestSchedule failed", exception)
                Result.failure(DomainError.NetworkError("Network error fetching schedule suggestions", exception))
            }
        )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get schedule suggestions: $sessionId", e)
        Result.failure(DomainError.NetworkError("Network error fetching schedule suggestions", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in suggestSchedule", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching schedule suggestions", e))
    }
}
