package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.datasource.SkillDataSource
import com.dexter.app.data.mapper.SkillMapper
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.Skill
import com.dexter.app.domain.repository.SkillRepository
import java.io.IOException

class SkillRepositoryImpl(
    private val dataSource: SkillDataSource,
    private val skillMapper: SkillMapper
) : SkillRepository {

    companion object { private const val TAG = "SkillRepository" }

    override suspend fun getSkillById(id: Long): Result<Skill> = try {
        val dto = dataSource.getSkillById(id)
        if (dto != null) {
            Result.success(skillMapper.mapToDomain(dto))
        } else {
            Result.failure(DomainError.UnknownError("Skill not found"))
        }
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get skill by id: $id", e)
        Result.failure(DomainError.NetworkError("Network error fetching skill $id", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getSkillById", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching skill $id", e))
    }

    override suspend fun getSkillsForUser(userId: Long): Result<List<Skill>> = try {
        val skills = dataSource.getSkillsByUserId(userId).map { skillMapper.mapToDomain(it) }
        Result.success(skills)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get skills for user: $userId", e)
        Result.failure(DomainError.NetworkError("Network error fetching skills for user $userId", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getSkillsForUser", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching skills for user $userId", e))
    }

    override suspend fun searchSkills(query: String): Result<List<Skill>> = try {
        Result.success(emptyList())
    } catch (e: IOException) {
        Log.e(TAG, "Failed to search skills: $query", e)
        Result.failure(DomainError.NetworkError("Network error searching skills", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in searchSkills", e)
        Result.failure(DomainError.UnknownError("Unknown error searching skills", e))
    }

    override suspend fun saveSkill(skill: Skill): Result<Skill> = try {
        Result.success(skill)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to save skill", e)
        Result.failure(DomainError.NetworkError("Network error saving skill", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in saveSkill", e)
        Result.failure(DomainError.UnknownError("Unknown error saving skill", e))
    }

    override suspend fun deleteSkill(id: Long): Result<Unit> = try {
        Result.success(Unit)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to delete skill: $id", e)
        Result.failure(DomainError.NetworkError("Network error deleting skill $id", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in deleteSkill", e)
        Result.failure(DomainError.UnknownError("Unknown error deleting skill $id", e))
    }
}
