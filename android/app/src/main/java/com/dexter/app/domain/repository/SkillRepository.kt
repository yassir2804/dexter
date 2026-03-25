package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Skill

/** Repository contract for skills. */
interface SkillRepository {
    suspend fun getSkillById(id: Long): Skill?
    suspend fun getSkillsForUser(userId: Long): List<Skill>
    suspend fun searchSkills(query: String): List<Skill>
    suspend fun saveSkill(skill: Skill): Skill
    suspend fun deleteSkill(id: Long)
}
