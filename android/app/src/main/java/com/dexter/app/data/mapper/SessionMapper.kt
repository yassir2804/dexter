package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.SessionDto
import com.dexter.app.domain.model.Session
import com.dexter.app.domain.model.SessionStatus
import java.util.Date
import javax.inject.Inject

/** Mapper for `SessionDto` ↔ `Session`. */
class SessionMapper @Inject constructor() {

    private fun parseStatus(value: String?): SessionStatus = when (value?.uppercase()) {
        "SCHEDULED" -> SessionStatus.SCHEDULED
        "COMPLETED" -> SessionStatus.COMPLETED
        "CANCELLED" -> SessionStatus.CANCELLED
        else -> SessionStatus.SCHEDULED
    }

    fun mapToDomain(dto: SessionDto): Session = Session(
        id = dto.id ?: 0L,
        matchId = dto.matchId ?: 0L,
        skillId = dto.skillId ?: 0L,
        sessionNumber = dto.sessionNumber ?: 0,
        title = dto.title ?: "",
        description = dto.description ?: "",
        status = parseStatus(dto.status),
        scheduledAt = dto.scheduledAt?.let { Date(it) },
        completedAt = dto.completedAt?.let { Date(it) },
        createdAt = Date(dto.createdAt ?: 0L),
        duration = dto.duration
    )

    fun mapToDto(domain: Session): SessionDto = SessionDto(
        id = domain.id,
        matchId = domain.matchId,
        skillId = domain.skillId,
        sessionNumber = domain.sessionNumber,
        title = domain.title,
        description = domain.description,
        status = domain.status.name,
        scheduledAt = domain.scheduledAt?.time,
        completedAt = domain.completedAt?.time,
        createdAt = domain.createdAt.time,
        duration = domain.duration
    )
}
