package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.SessionDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Retrofit API service for sessions (read + edit + complete + delete). */
interface SessionApiService {

    /** GET /sessions?matchId=... — todas las sesiones de un match. */
    @GET("sessions")
    suspend fun getSessions(
        @Query("matchId") matchId: Long
    ): Response<List<SessionDto>>

    @GET("sessions/{id}")
    suspend fun getSessionById(
        @Path("id") sessionId: Long
    ): Response<SessionDto>

    /** POST /sessions — crear sesion manualmente (admin/uso interno). */
    @POST("sessions")
    suspend fun createSession(
        @Body session: SessionDto
    ): Response<SessionDto>

    /** PUT /sessions/{id} — editar titulo, descripcion, scheduledAt, etc. */
    @PUT("sessions/{id}")
    suspend fun updateSession(
        @Path("id") sessionId: Long,
        @Body session: SessionDto
    ): Response<SessionDto>

    /**
     * PATCH /sessions/{id}/complete — marca COMPLETED, setea completedAt,
     * incrementa totalSessions de ambos participantes, notifica al estudiante
     * para dejar resena, y cierra el match si era la ultima.
     */
    @PATCH("sessions/{id}/complete")
    suspend fun completeSession(
        @Path("id") sessionId: Long
    ): Response<SessionDto>

    @GET("sessions/{id}/suggest-schedule")
    suspend fun suggestSchedule(
        @Path("id") sessionId: Long
    ): Response<List<ScheduleSlotDto>>

    @DELETE("sessions/{id}")
    suspend fun deleteSession(
        @Path("id") sessionId: Long
    ): Response<Unit>
}

data class ScheduleSlotDto(
    val day: String? = null,
    val time: String? = null
)
