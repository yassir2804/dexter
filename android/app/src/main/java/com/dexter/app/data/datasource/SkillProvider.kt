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
            // Alice (userId=1) — offered
            Skill(id = 1, userId = 1, name = "Guitarra", category = "Musica", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(id = 3, userId = 1, name = "Ingles", category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(id = 4, userId = 1, name = "Marketing", category = "Negocios", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            // Alice (userId=1) — sought
            Skill(id = 5, userId = 1, name = "Fotografia", category = "Arte", skillRole = SkillRole.SOUGHT, skillLevel = SkillLevel.BEGINNER),
            Skill(id = 6, userId = 1, name = "Piano", category = "Musica", skillRole = SkillRole.SOUGHT, skillLevel = SkillLevel.BEGINNER),
            // Bob (userId=2) — offered
            Skill(id = 2, userId = 2, name = "English", category = "Language", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(id = 7, userId = 2, name = "Fotografia", category = "Arte", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            // Bob (userId=2) — sought
            Skill(id = 8, userId = 2, name = "Guitarra", category = "Musica", skillRole = SkillRole.SOUGHT, skillLevel = SkillLevel.BEGINNER)
        )

        fun findSkillById(id: Long): Skill? = skillList.find { it.id == id }

        fun findAllSkills(): List<Skill> = skillList

        fun findSkillsByUserId(userId: Long): List<Skill> = skillList.filter { it.userId == userId }
    }
}
