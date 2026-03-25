package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.MessageDto
import com.dexter.app.domain.model.Message
import com.dexter.app.domain.model.MessageStatus
import java.util.Date

/** Mapper for `MessageDto` ↔ `Message`. */
class MessageMapper {

    private fun parseStatus(value: String?): MessageStatus = when (value?.uppercase()) {
        "SENT" -> MessageStatus.SENT
        "DELIVERED" -> MessageStatus.DELIVERED
        "READ" -> MessageStatus.READ
        else -> MessageStatus.SENT
    }

    fun mapToDomain(dto: MessageDto): Message = Message(
        id = dto.id ?: 0L,
        conversationId = dto.conversationId ?: 0L,
        senderId = dto.senderId ?: 0L,
        text = dto.text ?: "",
        status = parseStatus(dto.status),
        createdAt = Date(dto.createdAt ?: 0L)
    )

    fun mapToDto(domain: Message): MessageDto = MessageDto(
        id = domain.id,
        conversationId = domain.conversationId,
        senderId = domain.senderId,
        text = domain.text,
        status = domain.status.name,
        createdAt = domain.createdAt.time
    )
}
