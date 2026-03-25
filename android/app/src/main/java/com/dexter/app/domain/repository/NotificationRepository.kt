package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Notification

/** Repository contract for notifications. */
interface NotificationRepository {
    suspend fun getNotificationsForUser(userId: Long): List<Notification>
    suspend fun markAsRead(notificationId: Long)
}
