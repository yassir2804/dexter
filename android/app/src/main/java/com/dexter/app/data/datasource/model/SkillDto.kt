package com.dexter.app.data.datasource.model

/**
 * DTO for Skill objects exchanged with remote services.
 *
 * Note: `skillRole` and `skillLevel` are represented as strings in the DTO
 * and must be parsed into domain enums. Mapping functions provide safe
 * fallbacks when the incoming value is unexpected or null.
 */
data class SkillDto(
    val id: Long? = null,
    val userId: Long? = null,
    val name: String? = null,
    val category: String? = null,
    val skillRole: String? = null,
    val skillLevel: String? = null
)