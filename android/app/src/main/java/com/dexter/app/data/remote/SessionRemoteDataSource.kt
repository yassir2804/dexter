package com.dexter.app.data.remote

import com.dexter.app.data.datasource.model.SessionDto
import com.dexter.app.data.remote.api.ScheduleSlotDto
import com.dexter.app.data.remote.api.SessionApiService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import javax.inject.Inject

/** Remote data source for sessions read operations. */
class SessionRemoteDataSource @Inject constructor(
    private val sessionApiService: SessionApiService
) {
    companion object {
        private const val API_TIMEOUT_MS = 10_000L
    }

    suspend fun getSessions(matchId: Long): Result<List<SessionDto>> = safeApiCall {
        sessionApiService.getSessions(matchId)
    }

    suspend fun getSessionById(sessionId: Long): Result<SessionDto> = safeApiCall {
        sessionApiService.getSessionById(sessionId)
    }

    suspend fun createSession(dto: SessionDto): Result<SessionDto> = safeApiCall {
        sessionApiService.createSession(dto)
    }

    suspend fun updateSession(sessionId: Long, dto: SessionDto): Result<SessionDto> = safeApiCall {
        sessionApiService.updateSession(sessionId, dto)
    }

    suspend fun deleteSession(sessionId: Long): Result<Unit> = safeApiCall {
        sessionApiService.deleteSession(sessionId)
    }

    suspend fun completeSession(sessionId: Long): Result<SessionDto> = safeApiCall {
        sessionApiService.completeSession(sessionId)
    }

    suspend fun suggestSchedule(sessionId: Long): Result<List<ScheduleSlotDto>> = safeApiCall {
        sessionApiService.suggestSchedule(sessionId)
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
        Result.failure(Exception("Network timeout. Verify Mockoon is running on port 3000", e))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
