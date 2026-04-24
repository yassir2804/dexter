package com.dexter.backend.service

import com.dexter.backend.dto.PersonResultDto
import com.dexter.backend.dto.SkillSummaryDto

interface SearchService {

    /** Categorías únicas de todas las skills (ej Música, Idiomas, Arte). */
    fun findCategories(): List<String>

    /** Top 20 skills agrupadas por nombre, ordenadas por peopleCount desc. */
    fun findPopularSkills(): List<SkillSummaryDto>

    /** Filtra por coincidencia parcial en el nombre (case insensitive). */
    fun searchSkills(query: String): List<SkillSummaryDto>

    /** Filtra por categoría exacta (case insensitive). */
    fun filterByCategory(category: String): List<SkillSummaryDto>

    /** Personas que OFERTAN una skill cuyo nombre contenga el string dado. */
    fun findPeopleForSkill(skillName: String): List<PersonResultDto>
}
