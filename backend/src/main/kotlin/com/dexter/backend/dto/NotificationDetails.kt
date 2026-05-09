package com.dexter.backend.dto

data class NotificationDetails(
    val id: Long,
    val referenceId: Long?,
    val title: String,
    val body: String?,
    val notificationType: String,
    val isRead: Boolean,
    val createdAt: Long,
)
