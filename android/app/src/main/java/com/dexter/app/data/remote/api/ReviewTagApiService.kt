package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.ReviewTagDto
import com.dexter.app.data.datasource.model.SimpleResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/** Retrofit API service for review tags. */
interface ReviewTagApiService {

    @GET("review-tags")
    suspend fun getReviewTags(): Response<List<ReviewTagDto>>

    @POST("review-tags")
    suspend fun addReviewTag(
        @Body tag: ReviewTagDto
    ): Response<ReviewTagDto>

    @DELETE("review-tags/{id}")
    suspend fun removeReviewTag(
        @Path("id") tagId: Long
    ): Response<SimpleResponseDto>
}
