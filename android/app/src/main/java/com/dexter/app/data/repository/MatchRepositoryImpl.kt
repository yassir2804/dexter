package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.datasource.MatchDataSource
import com.dexter.app.data.mapper.MatchMapper
import com.dexter.app.data.mapper.MatchSummaryMapper
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.Match
import com.dexter.app.domain.model.MatchSummary
import com.dexter.app.domain.repository.MatchRepository
import java.io.IOException

class MatchRepositoryImpl(
    private val dataSource: MatchDataSource,
    private val matchMapper: MatchMapper,
    private val matchSummaryMapper: MatchSummaryMapper
) : MatchRepository {

    companion object { private const val TAG = "MatchRepository" }

    override suspend fun getMatchById(id: Long): Result<Match> = try {
        val dto = dataSource.getMatchById(id)
        if (dto != null) {
            Result.success(matchMapper.mapToDomain(dto))
        } else {
            Result.failure(DomainError.UnknownError("Match not found"))
        }
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get match by id: $id", e)
        Result.failure(DomainError.NetworkError("Network error fetching match $id", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getMatchById", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching match $id", e))
    }

    override suspend fun getMatchesForUser(userId: Long): Result<List<Match>> = try {
        val matches = dataSource.getMatchesForUser(userId).map { matchMapper.mapToDomain(it) }
        Result.success(matches)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get matches for user: $userId", e)
        Result.failure(DomainError.NetworkError("Network error fetching matches", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getMatchesForUser", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching matches", e))
    }

    override suspend fun createMatch(match: Match): Result<Match> = try {
        Result.success(match)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to create match", e)
        Result.failure(DomainError.NetworkError("Network error creating match", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in createMatch", e)
        Result.failure(DomainError.UnknownError("Unknown error creating match", e))
    }

    override suspend fun updateMatch(match: Match): Result<Match> = try {
        Result.success(match)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to update match", e)
        Result.failure(DomainError.NetworkError("Network error updating match", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in updateMatch", e)
        Result.failure(DomainError.UnknownError("Unknown error updating match", e))
    }

    override suspend fun getMatchSummaries(userId: Long): Result<List<MatchSummary>> = try {
        val summaries = dataSource.getMatchSummaries(userId).map { matchSummaryMapper.mapToDomain(it) }
        Result.success(summaries)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get match summaries for user: $userId", e)
        Result.failure(DomainError.NetworkError("Network error fetching match summaries", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getMatchSummaries", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching match summaries", e))
    }
}
