package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.ProfileSummaryDto
import com.dexter.app.data.datasource.model.RegisterRequestDto
import com.dexter.app.data.datasource.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * Retrofit API Service for user/profile operations.
 *
 * Endpoints:
 * - GET  /profile-summary?userId=...  → full profile with skills, stats, reviews
 * - POST /auth/register               → create a new user account
 *
 * Each method is a `suspend fun` so it works with Kotlin coroutines.
 * Returns `Response<T>` so we can check HTTP status codes in the RemoteDataSource.
 */
interface UserApiService {

    @GET("profile-summary")
    suspend fun getProfileSummary(
        @Query("userId") userId: Long
    ): Response<ProfileSummaryDto>

    /**
     * Registers a new user.
     *
     * @Body tells Retrofit to serialize [request] to JSON using Gson
     * and put it in the HTTP request body.
     *
     * Expected HTTP response: 201 Created with the created UserDto.
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<UserDto>

    /**
     * Updates an existing user profile.
     *
     * @PUT sends the modified profile as JSON body.
     * The server responds with the updated ProfileSummaryDto.
     */
    @PUT("profile-summary")
    suspend fun updateProfile(
        @Body profile: ProfileSummaryDto
    ): Response<ProfileSummaryDto>
}
