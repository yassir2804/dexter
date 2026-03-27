package com.dexter.app.data.datasource

import com.dexter.app.data.datasource.model.MatchDto
import com.dexter.app.data.mapper.MatchMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow



/**
 * MatchDataSource implementation backed by an in-memory provider.
 */
class MatchDataSourceImpl(
    private val matchMapper: MatchMapper
) : MatchDataSource {

    override suspend fun getMatches(): Flow<List<MatchDto>> = flow {
        val matches = MatchProvider.findAll()
        emit(matches.map { matchMapper.mapToDto(it) })
    }

    override suspend fun getMatchById(id: Long): MatchDto? =
        MatchProvider.findAll().find { it.id == id }?.let { matchMapper.mapToDto(it) }

    override suspend fun findMatchesForUser(userId: Long): Flow<List<MatchDto>> = flow {
        val matches = MatchProvider.findMatchesForUser(userId)
        emit(matches.map { matchMapper.mapToDto(it) })
    }

    override suspend fun insertMatch(matchDto: MatchDto) {
        // no-op for in-memory provider
    }

    override suspend fun updateMatch(matchDto: MatchDto) {
        // no-op for in-memory provider
    }
}
