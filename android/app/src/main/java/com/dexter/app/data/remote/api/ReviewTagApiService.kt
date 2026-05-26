package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.ReviewTagDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API service for review tags.
 *
 * El backend solo expone lectura sobre review_tags. Los tags se crean
 * implícitamente al enviar `tags: List<String>` dentro del body de
 * `POST /reviews`, no como entidades independientes. Por eso aquí
 * sólo hay GETs.
 *
 * Endpoints:
 *  - GET /review-tags?reviewId=X  → tags ligados a un review específico
 *  - GET /review-tags/catalog     → catálogo sugerido para el picker
 */
interface ReviewTagApiService {

    @GET("review-tags")
    suspend fun getTagsForReview(
        @Query("reviewId") reviewId: Long
    ): Response<List<ReviewTagDto>>

    @GET("review-tags/catalog")
    suspend fun getCatalog(): Response<List<String>>
}
