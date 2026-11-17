package com.dexter.backend.service

import com.dexter.backend.dto.PersonResult
import com.dexter.backend.dto.SkillSummaryDetails
import com.dexter.backend.entity.Match
import com.dexter.backend.entity.MatchStatus
import com.dexter.backend.entity.Skill
import com.dexter.backend.entity.SkillRole
import com.dexter.backend.repository.MatchRepository
import com.dexter.backend.repository.SkillRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AbstractSearchService(
    @Autowired val skillRepository: SkillRepository,
    @Autowired val matchRepository: MatchRepository,
) : SearchService {

    override fun findCategories(): List<String> =
        skillRepository.findAll()
            .mapNotNull { it.category }
            .distinct()
            .sorted()

    override fun findPopularSkills(): List<SkillSummaryDetails> =
        groupToSummaries(skillRepository.findAll())
            .sortedByDescending { it.peopleCount }
            .take(5)

    override fun searchSkills(query: String): List<SkillSummaryDetails> =
        groupToSummaries(skillRepository.findByNameContainingIgnoreCase(query))

    override fun filterByCategory(category: String): List<SkillSummaryDetails> =
        groupToSummaries(skillRepository.findByCategoryIgnoreCase(category))

    override fun findPeopleForSkill(skillName: String, viewerUserId: Long): List<PersonResult> {
        // Usuarios con quien el viewer ya tiene un match (cualquier estado salvo REJECTED):
        // a esos SI les puede seguir viendo el perfil aunque hayan ocultado su perfil,
        // porque ya hubo intencion de match entre las dos partes.
        val alreadyContactedIds = matchRepository
            .findByInitiatorIdOrReceiverId(viewerUserId, viewerUserId)
            .filter { it.status != MatchStatus.REJECTED }
            .flatMap { listOfNotNull(it.initiator?.id, it.receiver?.id) }
            .toSet()

        // Para evitar ofrecer matches duplicados, ocultamos los que ya estan PENDING o ACCEPTED.
        val activeMatchIds = matchRepository
            .findByInitiatorIdOrReceiverId(viewerUserId, viewerUserId)
            .filter { it.status == MatchStatus.PENDING || it.status == MatchStatus.ACCEPTED }
            .flatMap { listOfNotNull(it.initiator?.id, it.receiver?.id) }
            .toSet()

        val skills = skillRepository.findByNameContainingIgnoreCase(skillName)
            .filter { it.skillRole == SkillRole.OFFERED }
        return skills.mapNotNull { s ->
            val u = s.user ?: return@mapNotNull null
            val uid = u.id ?: return@mapNotNull null
            if (uid == viewerUserId || uid in activeMatchIds) return@mapNotNull null
            // Perfil oculto: solo se respeta para usuarios con los que NO hubo contacto previo
            if (u.profileVisible == false && uid !in alreadyContactedIds) return@mapNotNull null
            PersonResult(
                userId = uid,
                name = "${u.firstName ?: ""} ${u.lastName ?: ""}".trim(),
                avatarUrl = u.avatarUrl,
                initials = "${u.firstName?.firstOrNull() ?: ""}${u.lastName?.firstOrNull() ?: ""}".uppercase(),
                skillDescription = s.name,
                rating = u.rating ?: 0.0,
                sessionCount = u.totalSessions ?: 0,
                availability = "Disponible",
                aiScore = null,
                aiReason = null,
            )
        }.distinctBy { it.userId }
    }

    /** Agrupa skills por nombre y produce una lista de summaries con peopleCount distinto. */
    private fun groupToSummaries(skills: List<Skill>): List<SkillSummaryDetails> =
        skills.groupBy { it.name }
            .map { (name, list) ->
                val first = list.first()
                SkillSummaryDetails(
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
