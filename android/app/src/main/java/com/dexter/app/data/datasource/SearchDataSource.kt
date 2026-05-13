package com.dexter.app.data.datasource

import com.dexter.app.domain.model.PersonResult
import com.dexter.app.domain.model.SkillSummary

/**
 * DataSource contract for search operations.
 */
interface SearchDataSource {
    suspend fun getPopularSkills(): List<SkillSummary>
    suspend fun getCategories(): List<String>
    suspend fun searchSkills(query: String): List<SkillSummary>
    suspend fun filterSkillsByCategory(category: String): List<SkillSummary>
    suspend fun getPeopleForSkill(skillName: String, viewerUserId: Long): List<PersonResult>
}
