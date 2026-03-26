package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.SkillMapper
import com.dexter.app.domain.model.Skill
import com.dexter.app.domain.repository.SkillRepository
import java.io.IOException

class SkillRepositoryImpl(
    private val skillMapper: SkillMapper
) : SkillRepository {

    companion object { private const val TAG = "SkillRepository" }

    override suspend fun getSkillById(id: Long): Skill? = try {
        null
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get skill by id: $id", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getSkillById", e)
        null
    }

    override suspend fun getSkillsForUser(userId: Long): List<Skill> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get skills for user: $userId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getSkillsForUser", e)
        emptyList()
    }

    override suspend fun searchSkills(query: String): List<Skill> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to search skills: $query", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in searchSkills", e)
        emptyList()
    }

    override suspend fun saveSkill(skill: Skill): Skill = try {
        skill
    } catch (e: IOException) {
        Log.e(TAG, "Failed to save skill", e)
        skill
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in saveSkill", e)
        skill
    }

    override suspend fun deleteSkill(id: Long) { try {
        // no-op
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in deleteSkill", e)
    } }
}
