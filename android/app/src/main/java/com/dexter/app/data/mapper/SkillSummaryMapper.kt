package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.SkillSummaryDto
import com.dexter.app.domain.model.SkillIconType
import com.dexter.app.domain.model.SkillSummary
import javax.inject.Inject

/**
 * Maps [SkillSummaryDto] to [SkillSummary] domain model.
 */
class SkillSummaryMapper @Inject constructor() {

    fun mapToDomain(dto: SkillSummaryDto): SkillSummary = SkillSummary(
        name = dto.name ?: "",
        category = dto.category ?: "",
        peopleCount = dto.peopleCount ?: 0,
        iconType = parseIconType(dto.iconType)
    )

    fun mapToDomainList(dtos: List<SkillSummaryDto>): List<SkillSummary> =
        dtos.map { mapToDomain(it) }

    private fun parseIconType(value: String?): SkillIconType = when (value) {
        "music_note" -> SkillIconType.MUSIC
        "language"   -> SkillIconType.LANGUAGE
        "code"       -> SkillIconType.CODE
        "palette"    -> SkillIconType.DESIGN
        else         -> SkillIconType.MUSIC
    }
}
