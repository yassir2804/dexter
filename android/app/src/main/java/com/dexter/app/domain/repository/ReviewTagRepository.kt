package com.dexter.app.domain.repository

import com.dexter.app.domain.model.ReviewTag

/** Repository contract for review tags. */
interface ReviewTagRepository {
    suspend fun getTagsForReview(reviewId: Long): List<ReviewTag>
    suspend fun addTagToReview(reviewId: Long, tag: ReviewTag): ReviewTag
    suspend fun removeTag(tagId: Long)
}
