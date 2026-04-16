package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Session

/** Repository contract for sessions. */
interface SessionRepository {
    suspend fun getSessionById(id: Long): Result<Session>
    suspend fun getSessionsByMatch(matchId: Long): Result<List<Session>>
    suspend fun getActiveSession(sessionId: Long): Result<Session>
    suspend fun createSession(session: Session): Result<Session>
    suspend fun updateSession(session: Session): Result<Session>
    suspend fun cancelSession(sessionId: Long): Result<Unit>
    suspend fun completeSession(sessionId: Long, notes: String): Result<Session>
}
