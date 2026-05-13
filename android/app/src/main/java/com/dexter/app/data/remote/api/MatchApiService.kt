package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.MatchDetailDto
import com.dexter.app.data.datasource.model.MatchDto
import com.dexter.app.data.datasource.model.MatchRequestDto
import com.dexter.app.data.datasource.model.MatchSummaryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MatchApiService {

    /** GET /matches?userId={userId} — all summaries for the logged-in user. */
    @GET("matches")
    suspend fun getMatchSummaries(
        @Query("userId") userId: Long
    ): Response<List<MatchSummaryDto>>

    /** GET /matches/{matchId} — full match object. */
    @GET("matches/{matchId}")
    suspend fun getMatchById(
        @Path("matchId") matchId: Long
    ): Response<MatchDto>

    /** GET /matches?userId={userId}&status={status} — filtered by status. */
    @GET("matches")
    suspend fun getMatchesByStatus(
        @Query("userId") userId: Long,
        @Query("status") status: String
    ): Response<List<MatchDto>>

    /** POST /matches — initiate a new match. */
    @POST("matches")
    suspend fun createMatch(
        @Body body: MatchRequestDto
    ): Response<MatchSummaryDto>

    /** PATCH /matches/{id}/accept?viewerUserId={viewerUserId} — accept a pending match. */
    @PATCH("matches/{id}/accept")
    suspend fun acceptMatch(
        @Path("id") matchId: Long,
        @Query("viewerUserId") viewerUserId: Long
    ): Response<MatchSummaryDto>

    /** PATCH /matches/{id}/reject — reject a pending match. */
    @PATCH("matches/{id}/reject")
    suspend fun rejectMatch(
        @Path("id") matchId: Long
    ): Response<Unit>

    /** GET /matches/{matchId}/detail?viewerUserId={viewerUserId} — full detail for the detail screen. */
    @GET("matches/{matchId}/detail")
    suspend fun getMatchDetail(
        @Path("matchId") matchId: Long,
        @Query("viewerUserId") viewerUserId: Long
    ): Response<MatchDetailDto>

    /** GET /matches/suggestions?userId={userId} — top-5 suggestions with >50% compatibility. */
    @GET("matches/suggestions")
    suspend fun getSuggestions(
        @Query("userId") userId: Long
    ): Response<List<MatchSummaryDto>>

    /** POST /matches/{id}/ensure-sessions — generate sessions if none exist for an accepted match. */
    @POST("matches/{id}/ensure-sessions")
    suspend fun ensureSessions(
        @Path("id") matchId: Long
    ): Response<Unit>
}
