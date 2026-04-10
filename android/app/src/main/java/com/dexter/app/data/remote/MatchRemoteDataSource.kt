package com.dexter.app.data.remote

import com.dexter.app.data.datasource.model.MatchDto
import com.dexter.app.data.datasource.model.MatchSummaryDto
import com.dexter.app.data.remote.api.MatchApiService
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.Response
import javax.inject.Inject

/**
 * LAB 3 - RemoteDataSource (Data Layer) — Patrón basado en TaskApp
 *
 * RemoteDataSource es el intermediario que:
 * 1. Recibe solicitudes del Repository
 * 2. Llama a MatchApiService (Retrofit) para obtener datos
 * 3. Captura excepciones y las envuelve en Result<T>
 * 4. Retorna siempre Result<T> (nunca lanza excepciones descontroladas)
 *
 * Diferencia clave vs versión anterior:
 * - Usa Kotlin's built-in Result<T> (Result.success() / Result.failure())
 * - Usa helper safeApiCall() para evitar código repetido
 * - Mucho más limpio y legible (como TaskApp)
 *
 * Flujo:
 * ViewModel → Repository → RemoteDataSource.safeApiCall() → Retrofit → HTTP
 */
class MatchRemoteDataSource @Inject constructor(
    private val matchApiService: MatchApiService
) {
    companion object {
        private const val API_TIMEOUT_MS = 10_000L
    }

    /**
     * Obtiene todos los matches del usuario.
     *
     * @param userId ID del usuario
     * @return Result<List<MatchDto>> con datos o error
     */
    suspend fun getMatches(userId: Long): Result<List<MatchDto>> = safeApiCall {
        matchApiService.getMatches(userId)
    }

    /**
     * Obtiene un match específico por ID.
     */
    suspend fun getMatchById(matchId: Long): Result<MatchDto> = safeApiCall {
        matchApiService.getMatchById(matchId)
    }

    /**
     * Obtiene matches filtrados por estado.
     */
    suspend fun getMatchesByStatus(
        userId: Long,
        status: String
    ): Result<List<MatchDto>> = safeApiCall {
        matchApiService.getMatchesByStatus(userId, status)
    }

    /**
     * Obtiene los summaries de matches para la lista principal de Home.
     */
    suspend fun getMatchSummaries(userId: Long): Result<List<MatchSummaryDto>> = safeApiCall {
        matchApiService.getMatchSummaries(userId)
    }

    /**
     * Helper function: captura y maneja todos los errores de API en UN solo lugar.
     *
     * Esta es la "magia" que hace el código limpio:
     * - En lugar de try-catch en cada método, delegamos al helper
     * - El helper maneja: isSuccessful, null checks, excepciones
     * - Retorna siempre Result<T> (nunca lanza excepciones)
     *
     * @param apiCall El lambda que hace la llamada HTTP
     * @return Result.success(data) si 200 + body, Result.failure(e) si error
     */
    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<T>
    ): Result<T> = try {
        val response = withTimeout(API_TIMEOUT_MS) { apiCall() }
        if (response.isSuccessful) {
            response.body()?.let {
                Result.success(it)  // Kotlin's built-in Result
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
