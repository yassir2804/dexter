package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.NotificationDto
import com.dexter.app.data.datasource.model.SimpleResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

/** Retrofit API service for notifications endpoints. */
interface NotificationApiService {

    @GET("notifications")
    suspend fun getNotifications(
        @Query("userId") userId: Long
    ): Response<List<NotificationDto>>

    @PATCH("notifications/{id}/read")
    suspend fun markAsRead(
        @Path("id") notificationId: Long
    ): Response<SimpleResponseDto>
}
