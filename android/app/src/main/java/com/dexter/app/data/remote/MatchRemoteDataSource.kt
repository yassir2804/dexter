package com.dexter.app.data.remote

import com.dexter.app.data.datasource.model.MatchDetailDto
import com.dexter.app.data.datasource.model.MatchDto
import com.dexter.app.data.datasource.model.MatchRequestDto
import com.dexter.app.data.datasource.model.MatchSummaryDto
import com.dexter.app.data.remote.api.MatchApiService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import javax.inject.Inject

class MatchRemoteDataSource @Inject constructor(
    private val matchApiService: MatchApiService
) {
    companion object {
        private const val API_TIMEOUT_MS = 10_000L
    }

    suspend fun getMatchSummaries(userId: Long): Result<List<MatchSummaryDto>> = safeApiCall {
        matchApiService.getMatchSummaries(userId)
    }

    suspend fun getMatchById(matchId: Long): Result<MatchDto> = safeApiCall {
        matchApiService.getMatchById(matchId)
    }

    suspend fun getMatchesByStatus(userId: Long, status: String): Result<List<MatchDto>> = safeApiCall {
        matchApiService.getMatchesByStatus(userId, status)
    }

    suspend fun createMatch(initiatorId: Long, receiverId: Long, initiatorSkill: String? = null, receiverSkill: String? = null): Result<MatchSummaryDto> = safeApiCall {
        matchApiService.createMatch(MatchRequestDto(initiatorId, receiverId, initiatorSkill, receiverSkill))
    }

    suspend fun acceptMatch(matchId: Long, viewerUserId: Long, initiatorSkill: String? = null): Result<MatchSummaryDto> = safeApiCall {
        matchApiService.acceptMatch(matchId, viewerUserId, initiatorSkill)
    }

    suspend fun rejectMatch(matchId: Long): Result<Unit> = safeApiCall {
        matchApiService.rejectMatch(matchId)
    }

    suspend fun getMatchDetail(matchId: Long, viewerUserId: Long): Result<MatchDetailDto> = safeApiCall {
        matchApiService.getMatchDetail(matchId, viewerUserId)
    }

    suspend fun getSuggestions(userId: Long): Result<List<MatchSummaryDto>> = safeApiCall {
        matchApiService.getSuggestions(userId)
    }

    suspend fun completeMatch(matchId: Long): Result<MatchSummaryDto> = safeApiCall {
        matchApiService.completeMatch(matchId)
    }

    suspend fun ensureSessions(matchId: Long): Result<Unit> = try {
        val response = withTimeout(API_TIMEOUT_MS) { matchApiService.ensureSessions(matchId) }
        if (response.isSuccessful) Result.success(Unit)
        else Result.failure(Exception("API error ${response.code()}: ${response.errorBody()?.string()}"))
    } catch (e: TimeoutCancellationException) {
        Result.failure(Exception("Network timeout", e))
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Result<T> = try {
        val response = withTimeout(API_TIMEOUT_MS) { apiCall() }
        if (response.isSuccessful) {
            response.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Response body is null"))
        } else {
            val errorBody = response.errorBody()?.string()
            Result.failure(Exception("API error ${response.code()}: $errorBody"))
        }
    } catch (e: TimeoutCancellationException) {
        Result.failure(Exception("Network timeout", e))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
