package com.dexter.backend.service

import com.dexter.backend.dto.PersonResultDto
import com.dexter.backend.dto.SkillSummaryDto
import com.dexter.backend.entity.Skill
import com.dexter.backend.entity.SkillRole
import com.dexter.backend.repository.SkillRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AbstractSearchService(
    @Autowired val skillRepository: SkillRepository,
) : SearchService {

    override fun findCategories(): List<String> =
        skillRepository.findAll()
            .mapNotNull { it.category }
            .distinct()
            .sorted()

    override fun findPopularSkills(): List<SkillSummaryDto> =
        groupToSummaries(skillRepository.findAll())
            .sortedByDescending { it.peopleCount }
            .take(20)

    override fun searchSkills(query: String): List<SkillSummaryDto> =
        groupToSummaries(skillRepository.findByNameContainingIgnoreCase(query))

    override fun filterByCategory(category: String): List<SkillSummaryDto> =
        groupToSummaries(skillRepository.findByCategoryIgnoreCase(category))

    override fun findPeopleForSkill(skillName: String): List<PersonResultDto> {
        val skills = skillRepository.findByNameContainingIgnoreCase(skillName)
            .filter { it.skillRole == SkillRole.OFFERED }
        return skills.mapNotNull { s ->
            val u = s.user ?: return@mapNotNull null
            PersonResultDto(
                userId = u.id,
                name = "${u.firstName ?: ""} ${u.lastName ?: ""}".trim(),
                avatarUrl = u.avatarUrl,
                initials = "${u.firstName?.firstOrNull() ?: ""}${u.lastName?.firstOrNull() ?: ""}".uppercase(),
                skillDescription = "${s.name} — nivel ${s.skillLevel.name.lowercase()}",
                rating = u.rating ?: 0.0,
                sessionCount = u.totalSessions ?: 0,
                availability = "Disponible",
                aiScore = null,
                aiReason = null,
            )
        }
    }

    /** Agrupa skills por nombre y produce una lista de summaries con peopleCount distinto. */
    private fun groupToSummaries(skills: List<Skill>): List<SkillSummaryDto> =
        skills.groupBy { it.name }
            .map { (name, list) ->
                val first = list.first()
                SkillSummaryDto(
                    name = name,
                    category = first.category,
                    peopleCount = list.map { it.user?.id }.distinct().size,
                    iconType = iconFor(first.category),
                )
            }

    private fun iconFor(category: String?): String = when (category?.lowercase()) {
        "música" -> "music_note"
        "idiomas" -> "language"
        "programación" -> "code"
        "arte" -> "palette"
        else -> "school"
    }
}
