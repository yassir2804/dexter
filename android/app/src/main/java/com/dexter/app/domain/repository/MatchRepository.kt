package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Match

/** Repository contract for match operations. */
interface MatchRepository {
    suspend fun getMatchById(id: Long): Match?
    suspend fun getMatchesForUser(userId: Long): List<Match>
    suspend fun createMatch(match: Match): Match
    suspend fun updateMatch(match: Match): Match
}
