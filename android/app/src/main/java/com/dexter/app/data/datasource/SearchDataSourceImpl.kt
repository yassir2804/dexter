package com.dexter.app.data.datasource

import com.dexter.app.data.mapper.PersonResultMapper
import com.dexter.app.data.mapper.SkillSummaryMapper
import com.dexter.app.data.remote.SearchRemoteDataSource
import com.dexter.app.domain.model.PersonResult
import com.dexter.app.domain.model.SkillSummary
import javax.inject.Inject

/**
 * Implementation of [SearchDataSource].
 * Uses [SearchRemoteDataSource] for API calls and mappers for DTO → domain.
 * Falls back to [SearchProvider] if API is unavailable.
 */
class SearchDataSourceImpl @Inject constructor(
    private val remoteDataSource: SearchRemoteDataSource,
    private val skillSummaryMapper: SkillSummaryMapper,
    private val personResultMapper: PersonResultMapper
) : SearchDataSource {

    override suspend fun getPopularSkills(): List<SkillSummary> {
        val result = remoteDataSource.getPopularSkills()
        return result.getOrNull()?.let { skillSummaryMapper.mapToDomainList(it) }
            ?: SearchProvider.getPopularSkills()
    }

    override suspend fun getCategories(): List<String> {
        val result = remoteDataSource.getCategories()
        return result.getOrNull()
            ?: SearchProvider.getCategories()
    }

    override suspend fun searchSkills(query: String): List<SkillSummary> {
        val result = remoteDataSource.searchSkills(query)
        return result.getOrNull()?.let { skillSummaryMapper.mapToDomainList(it) }
            ?: SearchProvider.searchSkills(query)
    }

    override suspend fun filterSkillsByCategory(category: String): List<SkillSummary> {
        val result = remoteDataSource.filterSkillsByCategory(category)
        return result.getOrNull()?.let { skillSummaryMapper.mapToDomainList(it) }
            ?: SearchProvider.filterSkillsByCategory(category)
    }

    override suspend fun getPeopleForSkill(skillName: String): List<PersonResult> {
        val result = remoteDataSource.getPeopleForSkill(skillName)
        return result.getOrNull()?.let { personResultMapper.mapToDomainList(it) }
            ?: SearchProvider.getPeopleForSkill(skillName)
    }
}
