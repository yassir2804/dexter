package com.dexter.app.data.datasource.model

/**
 * DTO for a popular skill returned by the search API.
 * Maps directly to the JSON from GET /search/skills.
 */
data class SkillSummaryDto(
    val name: String?,
    val category: String?,
    val peopleCount: Int?,
    val iconType: String?
)
