package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.ReviewTagMapper
import com.dexter.app.domain.model.ReviewTag
import com.dexter.app.domain.repository.ReviewTagRepository
import java.io.IOException

class ReviewTagRepositoryImpl(
    private val reviewTagMapper: ReviewTagMapper
) : ReviewTagRepository {

    companion object { private const val TAG = "ReviewTagRepository" }

    override suspend fun getTagsForReview(reviewId: Long): List<ReviewTag> {
        return try {
            emptyList()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to get tags for review: $reviewId", e)
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error in getTagsForReview", e)
            emptyList()
        }
    }

    override suspend fun addTagToReview(reviewId: Long, tag: ReviewTag): ReviewTag {
        return try {
            tag
        } catch (e: IOException) {
            Log.e(TAG, "Failed to add tag to review: $reviewId", e)
            tag
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error in addTagToReview", e)
            tag
        }
    }

    override suspend fun removeTag(tagId: Long) {
        try {
            // no-op placeholder
        } catch (e: IOException) {
            Log.e(TAG, "Failed to remove tag: $tagId", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error in removeTag", e)
        }
    }
}
