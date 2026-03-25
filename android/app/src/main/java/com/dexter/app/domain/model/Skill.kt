package com.dexter.app.domain.model

import com.dexter.app.domain.model.SkillLevel
import com.dexter.app.domain.model.SkillRole

/**
 * Domain model for a user skill.
 *
 * @property id Skill identifier.
 * @property userId Owner user id (foreign reference).
 * @property name Human readable name of the skill.
 * @property category Optional category or group for the skill.
 * @property skillRole Whether the skill is offered or sought (`SkillRole`).
 * @property skillLevel Proficiency level (`SkillLevel`).
 */
data class Skill(
    val id: Long,
    val userId: Long,
    val name: String,
    val category: String?,
    val skillRole: SkillRole,
    val skillLevel: SkillLevel
)
