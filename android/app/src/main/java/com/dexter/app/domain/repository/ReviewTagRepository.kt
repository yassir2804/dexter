package com.dexter.app.domain.repository

import com.dexter.app.domain.model.ReviewTag

/** Repository contract for review tags. */
interface ReviewTagRepository {
    suspend fun getAvailableTags(): Result<List<ReviewTag>>
    suspend fun getTagsForReview(reviewId: Long): Result<List<ReviewTag>>
    suspend fun addTagToReview(reviewId: Long, tag: ReviewTag): Result<ReviewTag>
    suspend fun removeTag(tagId: Long): Result<Unit>
}
