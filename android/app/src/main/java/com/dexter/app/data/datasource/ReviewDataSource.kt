package com.dexter.app.data.datasource

import com.dexter.app.data.datasource.model.ReviewDto

/**
 * Data source interface for `Review` objects.
 */
interface ReviewDataSource {
    suspend fun getReviewsByUserId(userId: Long): List<ReviewDto>
    suspend fun getReviewById(id: Long): ReviewDto?
    suspend fun insertReview(reviewDto: ReviewDto)
}
