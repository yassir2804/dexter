package com.dexter.app.data.datasource

import com.dexter.app.data.datasource.model.MatchDto
import com.dexter.app.data.datasource.model.MatchSummaryDto
import com.dexter.app.data.mapper.MatchMapper
import com.dexter.app.data.mapper.MatchSummaryMapper
import com.dexter.app.data.remote.MatchRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * LAB 3 - MatchDataSourceImpl — Patrón basado en TaskApp
 *
 * ANTES: Usaba MatchProvider (datos mock hardcoded)
 * AHORA: Usa MatchRemoteDataSource (HTTP a Mockoon backend)
 *
 * Rol de MatchDataSourceImpl:
 * - Actúa como adaptador entre Repository y RemoteDataSource
 * - Mantiene compatibilidad con Flow<> para observables
 * - Delega lógica HTTP a RemoteDataSource
 * - Retorna la estructura MatchDto que espera Repository
 *
 * Flujo:
 * Repository → THIS (DataSourceImpl) → RemoteDataSource → Retrofit → HTTP
 */
class MatchDataSourceImpl @Inject constructor(
    private val remoteDataSource: MatchRemoteDataSource,
    private val matchMapper: MatchMapper,
    private val matchSummaryMapper: MatchSummaryMapper
) : MatchDataSource {

    private fun mapMatchToSummary(dto: MatchDto): MatchSummaryDto {
        val receiverId = dto.receiverId ?: 0L
        val compatibility = ((dto.compatibilityScore ?: 0.0).toInt()).coerceIn(0, 100)
        return MatchSummaryDto(
            initials = "U$receiverId",
            name = "Usuario $receiverId",
            avatarUrl = null,
            offers = "Skill offered",
            seeks = "Skill sought",
            rating = "4.5",
            sessions = 0,
            compatibilityPercent = "$compatibility%"
        )
    }

    /**
     * Obtiene todos los matches del usuario a través de HTTP.
     */
    override suspend fun getMatches(): Flow<List<MatchDto>> = flow {
        val result = remoteDataSource.getMatches(userId = 1L)  // TODO: userId dinámico
        result.onSuccess { emit(it) }
            .onFailure { throw it }
    }

    /**
     * Obtiene un match específico por ID a través de HTTP.
     */
    override suspend fun getMatchById(id: Long): MatchDto? {
        val result = remoteDataSource.getMatchById(id)
        return result.getOrNull()  // Retorna data si Success, null si Error
    }

    /**
     * Busca matches para un usuario específico a través de HTTP.
     */
    override suspend fun findMatchesForUser(userId: Long): Flow<List<MatchDto>> = flow {
        val result = remoteDataSource.getMatches(userId)
        result.onSuccess { emit(it) }
            .onFailure { throw it }
    }

    /**
     * Inserta un nuevo match (POST al backend).
     * TODO: Implementar cuando MatchApiService tenga @POST
     */
    override suspend fun insertMatch(matchDto: MatchDto) {
        // no-op for now
    }

    /**
     * Actualiza un match existente (PUT al backend).
     * TODO: Implementar cuando MatchApiService tenga @PUT
     */
    override suspend fun updateMatch(matchDto: MatchDto) {
        // no-op for now
    }

    /**
     * Obtiene matches para un usuario (retorna List, no Flow).
     */
    override suspend fun getMatchesForUser(userId: Long): List<MatchDto> {
        val result = remoteDataSource.getMatches(userId)
        return result.getOrNull() ?: emptyList()
    }

    /**
     * Obtiene resúmenes de matches para un usuario.
     */
    override suspend fun getMatchSummaries(userId: Long): List<MatchSummaryDto> {
        val summaryResult = remoteDataSource.getMatchSummaries(userId)
        val summaries = summaryResult.getOrNull()
        if (!summaries.isNullOrEmpty()) {
            return summaries
        }

        // Fallback: si summaries viene vacío o falla, intentamos con /matches.
        val matchResult = remoteDataSource.getMatches(userId)
        val matches = matchResult.getOrNull()
        if (matches != null) {
            return matches.map { mapMatchToSummary(it) }
        }

        // Importante: no devolver emptyList() en caso de error real.
        // Si devolvemos vacío, Home muestra "no hay matches" aunque la API falló.
        throw (summaryResult.exceptionOrNull()
            ?: matchResult.exceptionOrNull()
            ?: IllegalStateException("Unable to fetch match summaries"))
    }
}
