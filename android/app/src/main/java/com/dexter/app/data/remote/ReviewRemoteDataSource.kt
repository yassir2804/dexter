package com.dexter.app.data.remote

import com.dexter.app.data.datasource.model.ReviewDto
import com.dexter.app.data.remote.api.ReviewApiService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import javax.inject.Inject

/** Remote data source for reviews with centralized HTTP error handling. */
class ReviewRemoteDataSource @Inject constructor(
    private val reviewApiService: ReviewApiService
) {
    companion object {
        private const val API_TIMEOUT_MS = 10_000L
    }

    suspend fun getReviews(): Result<List<ReviewDto>> = safeApiCall {
        reviewApiService.getReviews()
    }

    suspend fun createReview(review: ReviewDto): Result<ReviewDto> = safeApiCall {
        reviewApiService.createReview(review)
    }

    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Result<T> = try {
        val response = withTimeout(API_TIMEOUT_MS) { apiCall() }
        if (response.isSuccessful) {
            response.body()?.let {
                Result.success(it)
            } ?: Result.failure(Exception("La respuesta del servidor llegó vacía. Intenta de nuevo."))
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
