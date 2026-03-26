package com.dexter.app.data.datasource

import com.dexter.app.domain.model.Skill
import com.dexter.app.domain.model.SkillLevel
import com.dexter.app.domain.model.SkillRole

/**
 * In-memory provider that simulates skill records.
 */
class SkillProvider {
    companion object {
        private val skillList = listOf(
            Skill(
                id = 1,
                userId = 1,
                name = "Guitar",
                category = "Music",
                skillRole = SkillRole.OFFERED,
                skillLevel = SkillLevel.ADVANCED
            ),
            Skill(
                id = 2,
                userId = 2,
                name = "English",
                category = "Language",
                skillRole = SkillRole.SOUGHT,
                skillLevel = SkillLevel.INTERMEDIATE
            )
        )

        fun findSkillById(id: Long): Skill? = skillList.find { it.id == id }

        fun findAllSkills(): List<Skill> = skillList
    }
}
