package com.dexter.backend.service

import com.dexter.backend.dto.PersonResult
import com.dexter.backend.dto.SkillSummaryDetails

interface SearchService {

    /** Categorías únicas de todas las skills (ej Música, Idiomas, Arte). */
    fun findCategories(): List<String>

    /** Top 20 skills agrupadas por nombre, ordenadas por peopleCount desc. */
    fun findPopularSkills(): List<SkillSummaryDetails>

    /** Filtra por coincidencia parcial en el nombre (case insensitive). */
    fun searchSkills(query: String): List<SkillSummaryDetails>

    /** Filtra por categoría exacta (case insensitive). */
    fun filterByCategory(category: String): List<SkillSummaryDetails>

    /** Personas que OFERTAN una skill cuyo nombre contenga el string dado. */
    fun findPeopleForSkill(skillName: String): List<PersonResult>
}
