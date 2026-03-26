package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Session

/** Repository contract for sessions. */
interface SessionRepository {
    suspend fun getSessionById(id: Long): Session?
    suspend fun getSessionsForMatch(matchId: Long): List<Session>
    suspend fun createSession(session: Session): Session
    suspend fun updateSession(session: Session): Session
}
