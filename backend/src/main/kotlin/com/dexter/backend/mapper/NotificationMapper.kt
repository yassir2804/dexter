package com.dexter.backend.mapper

import com.dexter.backend.dto.NotificationDetails
import com.dexter.backend.entity.Notification
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface NotificationMapper {

    @Mapping(target = "notificationType", expression = "java(notification.getNotificationType().name())")
    @Mapping(target = "createdAt", expression = "java(notification.getCreatedAt() != null ? notification.getCreatedAt().getTime() : 0L)")
    fun toDetails(notification: Notification): NotificationDetails

    fun toDetailsList(notifications: List<Notification>): List<NotificationDetails>
}
