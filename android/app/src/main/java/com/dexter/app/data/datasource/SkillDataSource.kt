package com.dexter.app.data.datasource

import com.dexter.app.data.datasource.model.SkillDto
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for `Skill` objects.
 */
interface SkillDataSource {
    suspend fun getSkills(): Flow<List<SkillDto>>
    suspend fun getSkillById(id: Long): SkillDto?
    suspend fun insertSkill(skillDto: SkillDto)
    suspend fun updateSkill(skillDto: SkillDto)
    suspend fun deleteSkill(skillDto: SkillDto)
}
