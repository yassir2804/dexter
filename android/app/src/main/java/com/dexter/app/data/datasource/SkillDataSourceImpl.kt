package com.dexter.app.data.datasource

import com.dexter.app.data.datasource.model.SkillDto
import com.dexter.app.data.mapper.SkillMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * SkillDataSource implementation backed by an in-memory provider.
 */
class SkillDataSourceImpl(
    private val skillMapper: SkillMapper
) : SkillDataSource {

    override suspend fun getSkills(): Flow<List<SkillDto>> = flow {
        val skills = SkillProvider.findAllSkills()
        emit(skills.map { skillMapper.mapToDto(it) })
    }

    override suspend fun getSkillById(id: Long): SkillDto? =
        SkillProvider.findSkillById(id)?.let { skillMapper.mapToDto(it) }

    override suspend fun insertSkill(skillDto: SkillDto) {
        // Mock implementation
    }

    override suspend fun updateSkill(skillDto: SkillDto) {
        // Mock implementation
    }

    override suspend fun deleteSkill(skillDto: SkillDto) {
        // Mock implementation
    }
}
