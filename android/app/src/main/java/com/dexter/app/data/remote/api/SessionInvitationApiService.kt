package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.SessionInvitationDto
import retrofit2.Response
import retrofit2.http.GET

/** Retrofit API service for session invitation read endpoints. */
interface SessionInvitationApiService {

    @GET("session-invitations")
    suspend fun getInvitations(): Response<List<SessionInvitationDto>>
}
