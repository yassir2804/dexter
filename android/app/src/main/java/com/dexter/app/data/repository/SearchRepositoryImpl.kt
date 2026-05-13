package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.datasource.SearchDataSource
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.PersonResult
import com.dexter.app.domain.model.SkillSummary
import com.dexter.app.domain.repository.SearchRepository
import java.io.IOException
import javax.inject.Inject

/**
 * Implementation of [SearchRepository].
 * Uses [SearchDataSource] to fetch search data and wraps results in [Result].
 */
class SearchRepositoryImpl @Inject constructor(
    private val dataSource: SearchDataSource
) : SearchRepository {

    companion object {
        private const val TAG = "SearchRepository"
    }

    override suspend fun getPopularSkills(): Result<List<SkillSummary>> = try {
        val skills = dataSource.getPopularSkills()
        Result.success(skills)
    } catch (e: IOException) {
        Log.e(TAG, "Network error fetching popular skills", e)
        Result.failure(DomainError.NetworkError("Network error fetching skills", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error fetching popular skills", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching skills", e))
    }

    override suspend fun getCategories(): Result<List<String>> = try {
        val categories = dataSource.getCategories()
        Result.success(categories)
    } catch (e: IOException) {
        Log.e(TAG, "Network error fetching categories", e)
        Result.failure(DomainError.NetworkError("Network error fetching categories", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error fetching categories", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching categories", e))
    }

    override suspend fun searchSkills(query: String): Result<List<SkillSummary>> = try {
        val skills = dataSource.searchSkills(query)
        Result.success(skills)
    } catch (e: IOException) {
        Log.e(TAG, "Network error searching skills: $query", e)
        Result.failure(DomainError.NetworkError("Network error searching skills", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error searching skills: $query", e)
        Result.failure(DomainError.UnknownError("Unknown error searching skills", e))
    }

    override suspend fun filterSkillsByCategory(category: String): Result<List<SkillSummary>> = try {
        val skills = dataSource.filterSkillsByCategory(category)
        Result.success(skills)
    } catch (e: IOException) {
        Log.e(TAG, "Network error filtering skills by category: $category", e)
        Result.failure(DomainError.NetworkError("Network error filtering skills", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error filtering skills by category: $category", e)
        Result.failure(DomainError.UnknownError("Unknown error filtering skills", e))
    }

    override suspend fun getPeopleForSkill(skillName: String, viewerUserId: Long): Result<List<PersonResult>> = try {
        val people = dataSource.getPeopleForSkill(skillName, viewerUserId)
        Result.success(people)
    } catch (e: IOException) {
        Log.e(TAG, "Network error fetching people for skill: $skillName", e)
        Result.failure(DomainError.NetworkError("Network error fetching people", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error fetching people for skill: $skillName", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching people", e))
    }
}
