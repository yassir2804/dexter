package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.PersonResultDto
import com.dexter.app.domain.model.PersonResult
import javax.inject.Inject

/**
 * Maps [PersonResultDto] to [PersonResult] domain model.
 */
class PersonResultMapper @Inject constructor() {

    fun mapToDomain(dto: PersonResultDto): PersonResult = PersonResult(
        userId = dto.userId ?: 0L,
        name = dto.name ?: "",
        avatarUrl = dto.avatarUrl,
        initials = dto.initials ?: "",
        skillDescription = dto.skillDescription ?: "",
        rating = dto.rating ?: 0.0,
        sessionCount = dto.sessionCount ?: 0,
        availability = dto.availability ?: "",
        aiScore = dto.aiScore ?: 0,
        aiReason = dto.aiReason ?: ""
    )

    fun mapToDomainList(dtos: List<PersonResultDto>): List<PersonResult> =
        dtos.map { mapToDomain(it) }
}
