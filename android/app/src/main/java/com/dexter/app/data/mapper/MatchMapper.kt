package com.dexter.app.data.mapper

import com.dexter.app.data.model.MatchDto
import com.dexter.app.domain.model.Match
import com.dexter.app.domain.model.MatchStatus
import java.util.Date

/** Mapper for `MatchDto` ↔ `Match`. */
class MatchMapper {

    private fun parseStatus(value: String?): MatchStatus = when (value?.uppercase()) {
        "PENDING" -> MatchStatus.PENDING
        "ACCEPTED" -> MatchStatus.ACCEPTED
        "REJECTED" -> MatchStatus.REJECTED
        "CANCELLED" -> MatchStatus.CANCELLED
        else -> MatchStatus.PENDING
    }

    fun mapToDomain(dto: MatchDto): Match = Match(
        id = dto.id ?: 0L,
        initiatorId = dto.initiatorId ?: 0L,
        receiverId = dto.receiverId ?: 0L,
        compatibilityScore = dto.compatibilityScore ?: 0.0,
        status = parseStatus(dto.status),
        createdAt = Date(dto.createdAt ?: 0L)
    )

    fun mapToDto(domain: Match): MatchDto = MatchDto(
        id = domain.id,
        initiatorId = domain.initiatorId,
        receiverId = domain.receiverId,
        compatibilityScore = domain.compatibilityScore,
        status = domain.status.name,
        createdAt = domain.createdAt.time
    )
}
