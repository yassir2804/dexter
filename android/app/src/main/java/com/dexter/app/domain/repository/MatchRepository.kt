package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Match

/** Repository contract for match operations. */
interface MatchRepository {
    suspend fun getMatchById(id: Long): Result<Match>
    suspend fun getMatchesForUser(userId: Long): Result<List<Match>>
    suspend fun createMatch(match: Match): Result<Match>
    suspend fun updateMatch(match: Match): Result<Match>
}
