package com.dexter.app.domain.model

/**
 * Summary of a skill for search display.
 * Shows the skill name, category, and how many people offer it.
 */
data class SkillSummary(
    val name: String,
    val category: String,
    val peopleCount: Int,
    val iconType: SkillIconType
)

/**
 * Determines which icon and color pair to use for a skill in the search list.
 */
enum class SkillIconType {
    MUSIC,
    LANGUAGE,
    CODE,
    CAMERA,
    DESIGN,
    MARKETING
}
