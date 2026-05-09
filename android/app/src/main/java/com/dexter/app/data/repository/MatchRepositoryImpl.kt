package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.datasource.MatchDataSource
import com.dexter.app.data.datasource.MatchProvider
import com.dexter.app.data.mapper.MatchMapper
import com.dexter.app.data.mapper.MatchSummaryMapper
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.Match
import com.dexter.app.domain.model.MatchDetail
import com.dexter.app.domain.model.MatchSummary
import com.dexter.app.domain.repository.MatchRepository
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val dataSource: MatchDataSource,
    private val matchMapper: MatchMapper,
    private val matchSummaryMapper: MatchSummaryMapper = MatchSummaryMapper()
) : MatchRepository {

    companion object { private const val TAG = "MatchRepository" }

    override suspend fun getMatchById(id: Long): Result<Match> = try {
        val dto = dataSource.getMatchById(id)
        if (dto != null) Result.success(matchMapper.mapToDomain(dto))
        else Result.failure(DomainError.UnknownError("Match not found"))
    } catch (e: IOException) {
        Result.failure(DomainError.NetworkError("Network error fetching match $id", e))
    } catch (e: Exception) {
        Result.failure(DomainError.UnknownError("Unknown error fetching match $id", e))
    }

    override suspend fun getMatchesForUser(userId: Long): Result<List<Match>> = try {
        val dtos = dataSource.findMatchesForUser(userId).first()
        Result.success(dtos.map { matchMapper.mapToDomain(it) })
    } catch (e: IOException) {
        Result.failure(DomainError.NetworkError("Network error fetching matches", e))
    } catch (e: Exception) {
        Result.failure(DomainError.UnknownError("Unknown error fetching matches", e))
    }

    override suspend fun getMatchSummaries(userId: Long): Result<List<MatchSummary>> = try {
        val dtos = dataSource.getMatchSummaries(userId)
        Result.success(dtos.map { matchSummaryMapper.mapToDomain(it) })
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get match summaries for user: $userId", e)
        Result.failure(DomainError.NetworkError("Network error fetching match summaries", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getMatchSummaries", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching match summaries", e))
    }

    override suspend fun getMatchDetail(matchId: Long): Result<MatchDetail> = try {
        Result.success(MatchProvider.findMatchDetailById(matchId))
    } catch (e: IOException) {
        Result.failure(DomainError.NetworkError("Network error fetching match detail", e))
    } catch (e: Exception) {
        Result.failure(DomainError.UnknownError("Unknown error fetching match detail", e))
    }

    override suspend fun initiateMatch(initiatorId: Long, receiverId: Long): Result<MatchSummary> = try {
        val dto = dataSource.createMatch(initiatorId, receiverId)
        Result.success(matchSummaryMapper.mapToDomain(dto))
    } catch (e: IOException) {
        Log.e(TAG, "Failed to initiate match", e)
        Result.failure(DomainError.NetworkError("Network error creating match", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in initiateMatch", e)
        Result.failure(DomainError.UnknownError("Unknown error creating match", e))
    }

    override suspend fun acceptMatch(matchId: Long, viewerUserId: Long): Result<MatchSummary> = try {
        val dto = dataSource.acceptMatch(matchId, viewerUserId)
        Result.success(matchSummaryMapper.mapToDomain(dto))
    } catch (e: IOException) {
        Log.e(TAG, "Failed to accept match $matchId", e)
        Result.failure(DomainError.NetworkError("Network error accepting match", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in acceptMatch", e)
        Result.failure(DomainError.UnknownError("Unknown error accepting match", e))
    }

    override suspend fun rejectMatch(matchId: Long): Result<Unit> = try {
        dataSource.rejectMatch(matchId)
        Result.success(Unit)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to reject match $matchId", e)
        Result.failure(DomainError.NetworkError("Network error rejecting match", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in rejectMatch", e)
        Result.failure(DomainError.UnknownError("Unknown error rejecting match", e))
    }
}
