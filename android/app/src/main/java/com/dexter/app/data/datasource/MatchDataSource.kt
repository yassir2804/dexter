package com.dexter.app.data.datasource

import com.dexter.app.data.datasource.model.MatchDetailDto
import com.dexter.app.data.datasource.model.MatchDto
import com.dexter.app.data.datasource.model.MatchSummaryDto
import kotlinx.coroutines.flow.Flow

interface MatchDataSource {
    suspend fun getMatches(): Flow<List<MatchDto>>
    suspend fun getMatchById(id: Long): MatchDto?
    suspend fun findMatchesForUser(userId: Long): Flow<List<MatchDto>>
    suspend fun insertMatch(matchDto: MatchDto)
    suspend fun updateMatch(matchDto: MatchDto)
    suspend fun getMatchesForUser(userId: Long): List<MatchDto>
    suspend fun getMatchSummaries(userId: Long): List<MatchSummaryDto>
    suspend fun createMatch(initiatorId: Long, receiverId: Long, initiatorSkill: String? = null, receiverSkill: String? = null): MatchSummaryDto
    suspend fun acceptMatch(matchId: Long, viewerUserId: Long, initiatorSkill: String? = null, timezone: String? = null): MatchSummaryDto
    suspend fun rejectMatch(matchId: Long)
    suspend fun getMatchDetail(matchId: Long, viewerUserId: Long): MatchDetailDto
    suspend fun getSuggestions(userId: Long): List<MatchSummaryDto>
    suspend fun completeMatch(matchId: Long): MatchSummaryDto
    suspend fun ensureSessions(matchId: Long)
}
