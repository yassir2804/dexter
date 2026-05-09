package com.dexter.app.data.remote

import com.dexter.app.data.datasource.model.NotificationDto
import com.dexter.app.data.remote.api.NotificationApiService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import javax.inject.Inject

/**
 * Remote data source for notifications.
 * Wraps Retrofit calls in Result<T> with consistent timeout/error handling.
 */
class NotificationRemoteDataSource @Inject constructor(
    private val notificationApiService: NotificationApiService
) {
    companion object {
        private const val API_TIMEOUT_MS = 10_000L
    }

    suspend fun getNotifications(): Result<List<NotificationDto>> = safeApiCall {
        notificationApiService.getNotifications()
    }

    suspend fun markAsRead(notificationId: Long): Result<Unit> =
        safeApiCall { notificationApiService.markAsRead(notificationId) }.map { Unit }

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
