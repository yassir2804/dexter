package com.dexter.app.data.remote

import com.dexter.app.data.datasource.model.PersonResultDto
import com.dexter.app.data.datasource.model.SkillSummaryDto
import com.dexter.app.data.remote.api.SearchApiService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import javax.inject.Inject

/**
 * Remote data source for search operations.
 * Wraps Retrofit calls in Result<T> using safeApiCall pattern.
 */
class SearchRemoteDataSource @Inject constructor(
    private val searchApiService: SearchApiService
) {
    companion object {
        private const val API_TIMEOUT_MS = 10_000L
    }

    suspend fun getCategories(): Result<List<String>> = safeApiCall {
        searchApiService.getCategories()
    }

    suspend fun getPopularSkills(): Result<List<SkillSummaryDto>> = safeApiCall {
        searchApiService.getPopularSkills()
    }

    suspend fun searchSkills(query: String): Result<List<SkillSummaryDto>> = safeApiCall {
        searchApiService.searchSkills(query)
    }

    suspend fun filterSkillsByCategory(category: String): Result<List<SkillSummaryDto>> = safeApiCall {
        searchApiService.filterSkillsByCategory(category)
    }

    suspend fun getPeopleForSkill(skillName: String, viewerUserId: Long): Result<List<PersonResultDto>> = safeApiCall {
        searchApiService.getPeopleForSkill(skillName, viewerUserId)
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
