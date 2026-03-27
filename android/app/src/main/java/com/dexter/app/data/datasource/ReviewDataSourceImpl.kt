package com.dexter.app.data.datasource

import com.dexter.app.data.datasource.model.ReviewDto
import com.dexter.app.data.mapper.ReviewMapper

/**
 * ReviewDataSource implementation backed by an in-memory provider.
 */
class ReviewDataSourceImpl(
    private val reviewMapper: ReviewMapper
) : ReviewDataSource {

    override suspend fun getReviewsByUserId(userId: Long): List<ReviewDto> =
        ReviewProvider.findReviewsByUserId(userId).map { reviewMapper.mapToDto(it) }

    override suspend fun getReviewById(id: Long): ReviewDto? =
        ReviewProvider.findReviewById(id)?.let { reviewMapper.mapToDto(it) }

    override suspend fun insertReview(reviewDto: ReviewDto) {
        // Mock implementation
    }
}
