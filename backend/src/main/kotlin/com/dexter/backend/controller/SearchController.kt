package com.dexter.backend.controller

import com.dexter.backend.dto.PersonResult
import com.dexter.backend.dto.SkillSummaryDetails
import com.dexter.backend.service.SearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${url.search}")
class SearchController(private val searchService: SearchService) {

    @GetMapping("/categories")
    fun categories(): List<String> = searchService.findCategories()

    /**
     * Un solo handler cubre las 3 variantes del front:
     *  - sin params → top skills populares
     *  - ?query=xxx → coincidencia parcial por nombre
     *  - ?category=xxx → filtro por categoría
     * query gana sobre category si ambos llegan.
     */
    @GetMapping("/skills")
    fun skills(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) category: String?,
    ): List<SkillSummaryDetails> = when {
        !query.isNullOrBlank() -> searchService.searchSkills(query)
        !category.isNullOrBlank() -> searchService.filterByCategory(category)
        else -> searchService.findPopularSkills()
    }

    @GetMapping("/people")
    fun people(
        @RequestParam skill: String,
        @RequestParam viewerUserId: Long,
    ): List<PersonResult> = searchService.findPeopleForSkill(skill, viewerUserId)
}
