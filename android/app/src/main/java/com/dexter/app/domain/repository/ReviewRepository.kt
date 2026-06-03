package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Review

/** Repository contract for session reviews. */
interface ReviewRepository {
    suspend fun getReviewById(id: Long): Result<Review>
    suspend fun getReviewsForSession(sessionId: Long): Result<List<Review>>
    suspend fun getReviewsForUser(userId: Long): Result<List<Review>>
    suspend fun createReview(review: Review): Result<Review>
}
