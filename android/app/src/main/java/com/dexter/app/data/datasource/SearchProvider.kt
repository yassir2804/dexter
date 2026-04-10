package com.dexter.app.data.datasource

import com.dexter.app.domain.model.PersonResult
import com.dexter.app.domain.model.SkillIconType
import com.dexter.app.domain.model.SkillSummary

/**
 * In-memory mock data provider for search functionality.
 * Provides popular skills and people offering each skill.
 */
object SearchProvider {

    private val categories = listOf(
        "Todas", "Idiomas", "Musica", "Tech", "Diseno", "Deporte"
    )

    private val popularSkills = listOf(
        SkillSummary("Guitarra", "Musica", 12, SkillIconType.MUSIC),
        SkillSummary("Ingles", "Idiomas", 24, SkillIconType.LANGUAGE),
        SkillSummary("Programacion", "Tech", 18, SkillIconType.CODE),
        SkillSummary("Fotografia", "Diseno", 9, SkillIconType.CAMERA),
        SkillSummary("Diseno Grafico", "Diseno", 7, SkillIconType.DESIGN),
        SkillSummary("Marketing Digital", "Tech", 5, SkillIconType.MARKETING)
    )

    private val guitarPeople = listOf(
        PersonResult(
            userId = 3L,
            name = "Sofia Rodriguez",
            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150",
            initials = "SR",
            skillDescription = "Guitarra clasica \u00b7 5 a\u00f1os",
            rating = 4.9,
            sessionCount = 47,
            availability = "Lun-Mie 18:00-20:00",
            aiScore = 98,
            aiReason = "Coincide 100% con tu disponibilidad"
        ),
        PersonResult(
            userId = 4L,
            name = "Miguel Torres",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
            initials = "MT",
            skillDescription = "Guitarra electrica \u00b7 8 a\u00f1os",
            rating = 4.8,
            sessionCount = 63,
            availability = "Mar-Jue 18:00-21:00",
            aiScore = 95,
            aiReason = "80% compatible con tus horarios"
        ),
        PersonResult(
            userId = 5L,
            name = "Ana Martinez",
            avatarUrl = null,
            initials = "AM",
            skillDescription = "Guitarra acustica \u00b7 3 a\u00f1os",
            rating = 4.7,
            sessionCount = 28,
            availability = "Vie-Sab 10:00-14:00",
            aiScore = 72,
            aiReason = "Disponibilidad parcial"
        ),
        PersonResult(
            userId = 6L,
            name = "Carlos Mendez",
            avatarUrl = null,
            initials = "CM",
            skillDescription = "Guitarra flamenca \u00b7 6 a\u00f1os",
            rating = 5.0,
            sessionCount = 89,
            availability = "Lun-Vie 08:00-10:00",
            aiScore = 65,
            aiReason = "Horarios matutinos"
        )
    )

    fun getCategories(): List<String> = categories

    fun getPopularSkills(): List<SkillSummary> = popularSkills

    fun searchSkills(query: String): List<SkillSummary> =
        popularSkills.filter { it.name.contains(query, ignoreCase = true) }

    fun filterSkillsByCategory(category: String): List<SkillSummary> =
        if (category == "Todas") popularSkills
        else popularSkills.filter { it.category.equals(category, ignoreCase = true) }

    fun getPeopleForSkill(skillName: String): List<PersonResult> = guitarPeople
}
