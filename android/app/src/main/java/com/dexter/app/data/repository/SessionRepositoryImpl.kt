package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.SessionMapper
import com.dexter.app.domain.model.Session
import com.dexter.app.domain.repository.SessionRepository
import java.io.IOException

class SessionRepositoryImpl(
    private val sessionMapper: SessionMapper
) : SessionRepository {

    companion object { private const val TAG = "SessionRepository" }

    override suspend fun getSessionById(id: Long): Session? = try {
        null
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get session by id: $id", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getSessionById", e)
        null
    }

    override suspend fun getSessionsForMatch(matchId: Long): List<Session> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get sessions for match: $matchId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getSessionsForMatch", e)
        emptyList()
    }

    override suspend fun createSession(session: Session): Session = try {
        session
    } catch (e: IOException) {
        Log.e(TAG, "Failed to create session", e)
        session
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in createSession", e)
        session
    }

    override suspend fun updateSession(session: Session): Session = try {
        session
    } catch (e: IOException) {
        Log.e(TAG, "Failed to update session", e)
        session
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in updateSession", e)
        session
    }
}
