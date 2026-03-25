package com.dexter.app.data.mapper

import com.dexter.app.data.model.SkillDto
import com.dexter.app.domain.model.Skill
import com.dexter.app.domain.model.SkillLevel
import com.dexter.app.domain.model.SkillRole

/**
 * Mapper for `SkillDto` ↔ `Skill` conversions.
 *
 * Parsing functions provide safe fallbacks for unexpected input from the API.
 */
class SkillMapper {

    private fun parseSkillRole(value: String?): SkillRole = when (value?.uppercase()) {
        "OFFERED" -> SkillRole.OFFERED
        "SOUGHT" -> SkillRole.SOUGHT
        else -> SkillRole.OFFERED
    }

    private fun parseSkillLevel(value: String?): SkillLevel = when (value?.uppercase()) {
        "BEGINNER" -> SkillLevel.BEGINNER
        "INTERMEDIATE" -> SkillLevel.INTERMEDIATE
        "ADVANCED" -> SkillLevel.ADVANCED
        else -> SkillLevel.BEGINNER
    }

    /** Map DTO → Domain */
    fun mapToDomain(dto: SkillDto): Skill = Skill(
        id = dto.id ?: 0L,
        userId = dto.userId ?: 0L,
        name = dto.name ?: "",
        category = dto.category,
        skillRole = parseSkillRole(dto.skillRole),
        skillLevel = parseSkillLevel(dto.skillLevel)
    )

    /** Map Domain → DTO */
    fun mapToDto(domain: Skill): SkillDto = SkillDto(
        id = domain.id,
        userId = domain.userId,
        name = domain.name,
        category = domain.category,
        skillRole = domain.skillRole.name,
        skillLevel = domain.skillLevel.name
    )
}