package com.dexter.app.data.datasource

import com.dexter.app.data.datasource.model.MatchDetailDto
import com.dexter.app.data.datasource.model.MatchDto
import com.dexter.app.data.datasource.model.MatchSummaryDto
import com.dexter.app.data.mapper.MatchMapper
import com.dexter.app.data.mapper.MatchSummaryMapper
import com.dexter.app.data.remote.MatchRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MatchDataSourceImpl @Inject constructor(
    private val remoteDataSource: MatchRemoteDataSource,
    private val matchMapper: MatchMapper,
    private val matchSummaryMapper: MatchSummaryMapper
) : MatchDataSource {

    override suspend fun getMatches(): Flow<List<MatchDto>> = flow {
        emit(emptyList())
    }

    override suspend fun getMatchById(id: Long): MatchDto? =
        remoteDataSource.getMatchById(id).getOrNull()

    override suspend fun findMatchesForUser(userId: Long): Flow<List<MatchDto>> = flow {
        val result = remoteDataSource.getMatchesByStatus(userId, "ALL")
        result.onSuccess { emit(it) }.onFailure { throw it }
    }

    override suspend fun insertMatch(matchDto: MatchDto) = Unit

    override suspend fun updateMatch(matchDto: MatchDto) = Unit

    override suspend fun getMatchesForUser(userId: Long): List<MatchDto> =
        remoteDataSource.getMatchesByStatus(userId, "ALL").getOrNull() ?: emptyList()

    override suspend fun getMatchSummaries(userId: Long): List<MatchSummaryDto> {
        val result = remoteDataSource.getMatchSummaries(userId)
        return result.getOrThrow()
    }

    override suspend fun createMatch(initiatorId: Long, receiverId: Long, initiatorSkill: String?, receiverSkill: String?): MatchSummaryDto {
        return remoteDataSource.createMatch(initiatorId, receiverId, initiatorSkill, receiverSkill).getOrThrow()
    }

    override suspend fun acceptMatch(matchId: Long, viewerUserId: Long, initiatorSkill: String?): MatchSummaryDto {
        return remoteDataSource.acceptMatch(matchId, viewerUserId, initiatorSkill).getOrThrow()
    }

    override suspend fun rejectMatch(matchId: Long) {
        remoteDataSource.rejectMatch(matchId).getOrThrow()
    }

    override suspend fun getMatchDetail(matchId: Long, viewerUserId: Long): MatchDetailDto =
        remoteDataSource.getMatchDetail(matchId, viewerUserId).getOrThrow()

    override suspend fun getSuggestions(userId: Long): List<MatchSummaryDto> =
        remoteDataSource.getSuggestions(userId).getOrElse { emptyList() }

    override suspend fun ensureSessions(matchId: Long) {
        remoteDataSource.ensureSessions(matchId).getOrThrow()
    }
}
