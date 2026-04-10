package com.dexter.app.domain.repository

import com.dexter.app.domain.model.PersonResult
import com.dexter.app.domain.model.SkillSummary

/**
 * Repository contract for search operations.
 * Provides popular skills, categories, and people offering a specific skill.
 */
interface SearchRepository {
    suspend fun getPopularSkills(): Result<List<SkillSummary>>
    suspend fun getCategories(): Result<List<String>>
    suspend fun searchSkills(query: String): Result<List<SkillSummary>>
    suspend fun filterSkillsByCategory(category: String): Result<List<SkillSummary>>
    suspend fun getPeopleForSkill(skillName: String): Result<List<PersonResult>>
}
