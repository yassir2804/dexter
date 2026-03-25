package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.NotificationMapper
import com.dexter.app.domain.model.Notification
import com.dexter.app.domain.repository.NotificationRepository
import java.io.IOException

class NotificationRepositoryImpl(
    private val notificationMapper: NotificationMapper
) : NotificationRepository {

    companion object { private const val TAG = "NotificationRepository" }

    override suspend fun getNotificationsForUser(userId: Long): List<Notification> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get notifications for user: $userId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getNotificationsForUser", e)
        emptyList()
    }

    override suspend fun markAsRead(notificationId: Long) {
        try {
            // no-op placeholder
        } catch (e: IOException) {
            Log.e(TAG, "Failed to mark notification as read: $notificationId", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error in markAsRead", e)
        }
    }
}
