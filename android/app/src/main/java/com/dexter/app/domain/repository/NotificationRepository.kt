package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Notification

/** Repository contract for notifications. */
interface NotificationRepository {
    suspend fun getNotificationsForUser(userId: Long): Result<List<Notification>>
    suspend fun markAsRead(notificationId: Long): Result<Unit>
    suspend fun markAllAsRead(userId: Long): Result<Unit>
}
