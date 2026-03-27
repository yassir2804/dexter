package com.dexter.app.data.datasource

import com.dexter.app.data.datasource.model.MatchDto
import kotlinx.coroutines.flow.Flow



/**
 * Data source interface for `Match` objects.
 */
interface MatchDataSource {
    suspend fun getMatches(): Flow<List<MatchDto>>
    suspend fun getMatchById(id: Long): MatchDto?
    suspend fun findMatchesForUser(userId: Long): Flow<List<MatchDto>>
    suspend fun insertMatch(matchDto: MatchDto)
    suspend fun updateMatch(matchDto: MatchDto)
}
