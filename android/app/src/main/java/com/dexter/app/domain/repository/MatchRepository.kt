package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Match
import com.dexter.app.domain.model.MatchDetail
import com.dexter.app.domain.model.MatchSummary

interface MatchRepository {
    suspend fun getMatchById(id: Long): Result<Match>
    suspend fun getMatchesForUser(userId: Long): Result<List<Match>>
    suspend fun getMatchSummaries(userId: Long): Result<List<MatchSummary>>
    suspend fun getMatchDetail(matchId: Long): Result<MatchDetail>
    suspend fun initiateMatch(initiatorId: Long, receiverId: Long): Result<MatchSummary>
    suspend fun acceptMatch(matchId: Long, viewerUserId: Long): Result<MatchSummary>
    suspend fun rejectMatch(matchId: Long): Result<Unit>
}
