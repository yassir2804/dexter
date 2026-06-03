package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.NotificationDto
import com.dexter.app.domain.model.Notification
import com.dexter.app.domain.model.NotificationType
import java.util.Date
import javax.inject.Inject

/** Mapper for `NotificationDto` ↔ `Notification`. */
class NotificationMapper @Inject constructor() {

    private fun parseType(value: String?): NotificationType = when (value?.uppercase()) {
        "NEW_MATCH" -> NotificationType.NEW_MATCH
        "SESSION_REMINDER" -> NotificationType.SESSION_REMINDER
        "NEW_REVIEW" -> NotificationType.NEW_REVIEW
        "AI_SUGGESTION" -> NotificationType.AI_SUGGESTION
        else -> NotificationType.NEW_MATCH
    }

    fun mapToDomain(dto: NotificationDto): Notification = Notification(
        id = dto.id ?: 0L,
        userId = dto.userId ?: 0L,
        referenceId = dto.referenceId,
        title = dto.title ?: "",
        body = dto.body ?: "",
        notificationType = parseType(dto.notificationType),
        isRead = dto.isRead ?: false,
        createdAt = Date(dto.createdAt ?: 0L)
    )

    fun mapToDto(domain: Notification): NotificationDto = NotificationDto(
        id = domain.id,
        userId = domain.userId,
        referenceId = domain.referenceId,
        title = domain.title,
        body = domain.body,
        notificationType = domain.notificationType.name,
        isRead = domain.isRead,
        createdAt = domain.createdAt.time
    )
}
