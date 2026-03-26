package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.AvailabilityDto
import com.dexter.app.domain.model.Availability

/** Mapper for `AvailabilityDto` ↔ `Availability`. */
class AvailabilityMapper {

    fun mapToDomain(dto: AvailabilityDto): Availability = Availability(
        id = dto.id ?: 0L,
        userId = dto.userId ?: 0L,
        dayOfWeek = dto.dayOfWeek ?: "",
        startTime = dto.startTime ?: "",
        endTime = dto.endTime ?: ""
    )

    fun mapToDto(domain: Availability): AvailabilityDto = AvailabilityDto(
        id = domain.id,
        userId = domain.userId,
        dayOfWeek = domain.dayOfWeek,
        startTime = domain.startTime,
        endTime = domain.endTime
    )
}