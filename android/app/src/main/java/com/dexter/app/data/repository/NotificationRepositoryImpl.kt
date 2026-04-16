package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.NotificationMapper
import com.dexter.app.data.remote.NotificationRemoteDataSource
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.Notification
import com.dexter.app.domain.repository.NotificationRepository
import java.io.IOException
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val remoteDataSource: NotificationRemoteDataSource,
    private val notificationMapper: NotificationMapper
) : NotificationRepository {

    companion object { private const val TAG = "NotificationRepository" }

    override suspend fun getNotificationsForUser(userId: Long): Result<List<Notification>> = try {
        remoteDataSource.getNotifications().map { dtos ->
            dtos
                .map { notificationMapper.mapToDomain(it) }
                .filter { it.userId == userId }
        }
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get notifications for user: $userId", e)
        Result.failure(DomainError.NetworkError("Network error fetching notifications", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getNotificationsForUser", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching notifications", e))
    }

    override suspend fun markAsRead(notificationId: Long): Result<Unit> = try {
        remoteDataSource.markAsRead(notificationId)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to mark notification as read: $notificationId", e)
        Result.failure(DomainError.NetworkError("Network error marking as read", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in markAsRead", e)
        Result.failure(DomainError.UnknownError("Unknown error marking as read", e))
    }

    override suspend fun markAllAsRead(userId: Long): Result<Unit> = try {
        remoteDataSource.getNotifications()
            .fold(
                onSuccess = { dtos ->
                    val targetIds = dtos
                        .filter { it.userId == userId && it.isRead != true }
                        .mapNotNull { it.id }

                    for (notificationId in targetIds) {
                        remoteDataSource.markAsRead(notificationId)
                            .onFailure { exception ->
                                return Result.failure(
                                    DomainError.NetworkError(
                                        "Network error marking all as read",
                                        exception
                                    )
                                )
                            }
                    }

                    Result.success(Unit)
                },
                onFailure = { exception ->
                    Result.failure(
                        DomainError.NetworkError("Network error marking all as read", exception)
                    )
                }
            )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to mark all as read", e)
        Result.failure(DomainError.NetworkError("Network error marking all as read", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in markAllAsRead", e)
        Result.failure(DomainError.UnknownError("Unknown error marking all as read", e))
    }
}
