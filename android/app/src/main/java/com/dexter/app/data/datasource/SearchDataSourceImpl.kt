package com.dexter.app.data.datasource

import com.dexter.app.data.mapper.PersonResultMapper
import com.dexter.app.data.mapper.SkillSummaryMapper
import com.dexter.app.data.remote.SearchRemoteDataSource
import com.dexter.app.domain.model.PersonResult
import com.dexter.app.domain.model.SkillSummary
import javax.inject.Inject

/**
 * Implementation of [SearchDataSource].
 *
 * Uses [SearchRemoteDataSource] for API calls and mappers for DTO → domain.
 * No fallback — if the API fails, the error propagates to the UI
 * so error states are visible (useful for testing with Mockoon error scenarios).
 */
class SearchDataSourceImpl @Inject constructor(
    private val remoteDataSource: SearchRemoteDataSource,
    private val skillSummaryMapper: SkillSummaryMapper,
    private val personResultMapper: PersonResultMapper
) : SearchDataSource {

    companion object {
        private const val TAG = "SearchDataSource"
    }

    override suspend fun getPopularSkills(): List<SkillSummary> {
        val result = remoteDataSource.getPopularSkills()
        return result.getOrElse { throw it }
            .let { skillSummaryMapper.mapToDomainList(it) }
    }

    override suspend fun getCategories(): List<String> {
        val result = remoteDataSource.getCategories()
        return result.getOrElse { throw it }
    }

    override suspend fun searchSkills(query: String): List<SkillSummary> {
        val result = remoteDataSource.searchSkills(query)
        return result.getOrElse { throw it }
            .let { skillSummaryMapper.mapToDomainList(it) }
    }

    override suspend fun filterSkillsByCategory(category: String): List<SkillSummary> {
        val result = remoteDataSource.filterSkillsByCategory(category)
        return result.getOrElse { throw it }
            .let { skillSummaryMapper.mapToDomainList(it) }
    }

    override suspend fun getPeopleForSkill(skillName: String): List<PersonResult> {
        val result = remoteDataSource.getPeopleForSkill(skillName)
        return result.getOrElse { throw it }
            .let { personResultMapper.mapToDomainList(it) }
    }
}
