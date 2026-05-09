package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.ReviewDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/** Retrofit API service for reviews. */
interface ReviewApiService {

    @GET("reviews")
    suspend fun getReviews(): Response<List<ReviewDto>>

    @POST("reviews")
    suspend fun createReview(
        @Body review: ReviewDto
    ): Response<ReviewDto>
}
