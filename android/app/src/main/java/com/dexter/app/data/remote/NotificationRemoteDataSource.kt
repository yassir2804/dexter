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

    suspend fun getNotifications(userId: Long): Result<List<NotificationDto>> = safeApiCall {
        notificationApiService.getNotifications(userId)
    }

    suspend fun markAsRead(notificationId: Long): Result<Unit> = try {
        val response = withTimeout(API_TIMEOUT_MS) {
            notificationApiService.markAsRead(notificationId)
        }
        if (response.isSuccessful) Result.success(Unit)
        else Result.failure(Exception(ApiErrorParser.toMessage(response.code(), null)))
    } catch (e: Exception) {
        Result.failure(e)
    }

    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Result<T> = try {
        val response = withTimeout(API_TIMEOUT_MS) { apiCall() }
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Result.success(body)
            } else {
                // 204 No Content / body vacío es éxito válido (ej. markAsRead).
                @Suppress("UNCHECKED_CAST")
                Result.success(Unit as T)
            }
        } else {
            val errorBody = response.errorBody()?.string()
            Result.failure(Exception(ApiErrorParser.toMessage(response.code(), errorBody)))
        }
    } catch (e: TimeoutCancellationException) {
        Result.failure(Exception("Tiempo de espera agotado. Revisa tu conexión a internet.", e))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
