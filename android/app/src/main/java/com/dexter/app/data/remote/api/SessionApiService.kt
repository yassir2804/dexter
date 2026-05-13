package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.SessionDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Retrofit API service for session read endpoints. */
interface SessionApiService {

    @GET("sessions")
    suspend fun getSessions(
        @Query("matchId") matchId: Long
    ): Response<List<SessionDto>>

    @GET("sessions/{id}")
    suspend fun getSessionById(
        @Path("id") sessionId: Long
    ): Response<SessionDto>
}
