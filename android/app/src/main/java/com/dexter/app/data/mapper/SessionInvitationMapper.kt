package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.SessionInvitationDto
import com.dexter.app.domain.model.SessionInvitation
import com.dexter.app.domain.model.InvitationStatus
import java.util.Date

/** Mapper for `SessionInvitationDto` ↔ `SessionInvitation`. */
class SessionInvitationMapper {

    private fun parseStatus(value: String?): InvitationStatus = when (value?.uppercase()) {
        "PENDING" -> InvitationStatus.PENDING
        "ACCEPTED" -> InvitationStatus.ACCEPTED
        "REJECTED" -> InvitationStatus.REJECTED
        "EXPIRED" -> InvitationStatus.EXPIRED
        else -> InvitationStatus.PENDING
    }

    fun mapToDomain(dto: SessionInvitationDto): SessionInvitation = SessionInvitation(
        id = dto.id ?: 0L,
        senderId = dto.senderId ?: 0L,
        receiverId = dto.receiverId ?: 0L,
        matchId = dto.matchId ?: 0L,
        status = parseStatus(dto.status),
        expiresAt = dto.expiresAt?.let { Date(it) },
        createdAt = Date(dto.createdAt ?: 0L)
    )

    fun mapToDto(domain: SessionInvitation): SessionInvitationDto = SessionInvitationDto(
        id = domain.id,
        senderId = domain.senderId,
        receiverId = domain.receiverId,
        matchId = domain.matchId,
        status = domain.status.name,
        expiresAt = domain.expiresAt?.time,
        createdAt = domain.createdAt.time
    )
}
