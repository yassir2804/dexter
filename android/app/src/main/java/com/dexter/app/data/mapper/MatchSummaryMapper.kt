package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.MatchSummaryDto
import com.dexter.app.domain.model.MatchSummary

/** Mapper for MatchSummaryDto <-> MatchSummary. */
class MatchSummaryMapper {

    fun mapToDomain(dto: MatchSummaryDto): MatchSummary = MatchSummary(
        initials = dto.initials ?: "",
        name = dto.name ?: "",
        avatarUrl = dto.avatarUrl,
        offers = dto.offers ?: "",
        seeks = dto.seeks ?: "",
        rating = dto.rating ?: "0.0",
        sessions = dto.sessions ?: 0,
        compatibilityPercent = dto.compatibilityPercent ?: "0%"
    )

    fun mapToDto(domain: MatchSummary): MatchSummaryDto = MatchSummaryDto(
        initials = domain.initials,
        name = domain.name,
        avatarUrl = domain.avatarUrl,
        offers = domain.offers,
        seeks = domain.seeks,
        rating = domain.rating,
        sessions = domain.sessions,
        compatibilityPercent = domain.compatibilityPercent
    )
}
