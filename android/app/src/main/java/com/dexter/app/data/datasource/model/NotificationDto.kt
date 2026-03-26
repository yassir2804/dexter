package com.dexter.app.data.datasource.model

/** DTO for Notification objects exchanged with the backend. */
data class NotificationDto(
    val id: Long? = null,
    val userId: Long? = null,
    val referenceId: Long? = null,
    val title: String? = null,
    val body: String? = null,
    val notificationType: String? = null,
    val isRead: Boolean? = null,
    val createdAt: Long? = null
)
