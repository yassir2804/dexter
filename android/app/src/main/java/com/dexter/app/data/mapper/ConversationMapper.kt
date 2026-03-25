package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.ConversationDto
import com.dexter.app.domain.model.Conversation
import java.util.Date

/** Mapper for `ConversationDto` ↔ `Conversation`. */
class ConversationMapper {

    fun mapToDomain(dto: ConversationDto): Conversation = Conversation(
        id = dto.id ?: 0L,
        user1Id = dto.user1Id ?: 0L,
        user2Id = dto.user2Id ?: 0L,
        matchId = dto.matchId,
        lastMessage = dto.lastMessage,
        lastMessageAt = dto.lastMessageAt?.let { Date(it) },
        createdAt = Date(dto.createdAt ?: 0L)
    )

    fun mapToDto(domain: Conversation): ConversationDto = ConversationDto(
        id = domain.id,
        user1Id = domain.user1Id,
        user2Id = domain.user2Id,
        matchId = domain.matchId,
        lastMessage = domain.lastMessage,
        lastMessageAt = domain.lastMessageAt?.time,
        createdAt = domain.createdAt.time
    )
}
