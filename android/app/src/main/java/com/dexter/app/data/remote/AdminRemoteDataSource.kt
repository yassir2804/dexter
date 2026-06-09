package com.dexter.app.data.remote

import com.dexter.app.data.datasource.model.UserDto
import com.dexter.app.data.remote.api.AdminApiService
import com.dexter.app.data.remote.api.ChangeRoleRequestDto
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import javax.inject.Inject

class AdminRemoteDataSource @Inject constructor(
    private val adminApiService: AdminApiService,
) {
    companion object {
        private const val API_TIMEOUT_MS = 10_000L
    }

    suspend fun getUsers(): Result<List<UserDto>> = safeApiCall {
        adminApiService.getUsers()
    }

    suspend fun toggleUserStatus(userId: Long): Result<UserDto> = safeApiCall {
        adminApiService.toggleUserStatus(userId)
    }

    suspend fun changeUserRole(userId: Long, roleName: String): Result<UserDto> = safeApiCall {
        adminApiService.changeUserRole(userId, ChangeRoleRequestDto(roleName))
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
