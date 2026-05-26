package com.dexter.app.data.remote

import com.dexter.app.data.datasource.model.ReviewTagDto
import com.dexter.app.data.remote.api.ReviewTagApiService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import javax.inject.Inject

/**
 * Remote data source for review tags. El backend solo expone lectura
 * sobre la tabla `review_tag`. Los tags se crean implicitamente al hacer
 * POST /reviews con la lista de `tags` en el body, no como entidades
 * sueltas — por eso aqui solo hay GETs.
 */
class ReviewTagRemoteDataSource @Inject constructor(
    private val reviewTagApiService: ReviewTagApiService
) {
    companion object {
        private const val API_TIMEOUT_MS = 10_000L
    }

    suspend fun getTagsForReview(reviewId: Long): Result<List<ReviewTagDto>> = safeApiCall {
        reviewTagApiService.getTagsForReview(reviewId)
    }

    suspend fun getCatalog(): Result<List<String>> = safeApiCall {
        reviewTagApiService.getCatalog()
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
        Result.failure(Exception("Network timeout — backend on port 3000?", e))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
