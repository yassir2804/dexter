package com.dexter.app.data.remote

import com.dexter.app.data.datasource.model.ProfileSummaryDto
import com.dexter.app.data.datasource.model.RegisterRequestDto
import com.dexter.app.data.datasource.model.UserDto
import com.dexter.app.data.remote.api.UserApiService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import javax.inject.Inject

/**
 * Remote data source for user/profile operations.
 *
 * Wraps Retrofit calls in [Result] using the [safeApiCall] pattern,
 * identical to [SearchRemoteDataSource].
 *
 * This class is the boundary between "network world" (exceptions, HTTP codes)
 * and "app world" (Result.success / Result.failure). Nothing above this layer
 * ever sees a raw exception from the network.
 */
class UserRemoteDataSource @Inject constructor(
    private val userApiService: UserApiService
) {
    companion object {
        private const val API_TIMEOUT_MS = 10_000L
    }

    suspend fun getProfileSummary(userId: Long): Result<ProfileSummaryDto> = safeApiCall {
        userApiService.getProfileSummary(userId)
    }

    /**
     * Sends a POST /auth/register with the registration data.
     * The server responds with the created UserDto (including generated ID).
     * Same safeApiCall pattern — works for POST exactly like for GET.
     */
    suspend fun register(request: RegisterRequestDto): Result<UserDto> = safeApiCall {
        userApiService.register(request)
    }

    suspend fun updateProfile(profile: ProfileSummaryDto): Result<ProfileSummaryDto> = safeApiCall {
        userApiService.updateProfile(profile)
    }

    /**
     * Wraps a Retrofit call with:
     * 1. Timeout (10s) — prevents infinite hangs if server is down
     * 2. HTTP status check — 200-299 = success, else error
     * 3. Null body check — 200 with empty body = error
     * 4. Exception catch — network errors, timeouts, parse errors
     *
     * Returns Result.success(body) or Result.failure(exception), NEVER throws.
     */
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
