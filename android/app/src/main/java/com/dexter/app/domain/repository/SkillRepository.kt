package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Skill

/** Repository contract for skills. */
interface SkillRepository {
    suspend fun getSkillById(id: Long): Result<Skill>
    suspend fun getSkillsForUser(userId: Long): Result<List<Skill>>
    suspend fun searchSkills(query: String): Result<List<Skill>>
    suspend fun saveSkill(skill: Skill): Result<Skill>
    suspend fun deleteSkill(id: Long): Result<Unit>
}
