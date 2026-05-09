package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.ReviewTagMapper
import com.dexter.app.data.remote.ReviewTagRemoteDataSource
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.ReviewTag
import com.dexter.app.domain.repository.ReviewTagRepository
import java.io.IOException
import javax.inject.Inject

class ReviewTagRepositoryImpl @Inject constructor(
    private val remoteDataSource: ReviewTagRemoteDataSource,
    private val reviewTagMapper: ReviewTagMapper
) : ReviewTagRepository {

    companion object { private const val TAG = "ReviewTagRepository" }

    override suspend fun getAvailableTags(): Result<List<ReviewTag>> = try {
        remoteDataSource.getReviewTags().map { dtos ->
            dtos.map { reviewTagMapper.mapToDomain(it) }
        }
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get available tags", e)
        Result.failure(DomainError.NetworkError("Network error fetching tags", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getAvailableTags", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching tags", e))
    }

    override suspend fun getTagsForReview(reviewId: Long): Result<List<ReviewTag>> = try {
        remoteDataSource.getReviewTags().map { dtos ->
            dtos
                .filter { it.reviewId == reviewId }
                .map { reviewTagMapper.mapToDomain(it) }
        }
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get tags for review: $reviewId", e)
        Result.failure(DomainError.NetworkError("Network error fetching review tags", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getTagsForReview", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching review tags", e))
    }

    override suspend fun addTagToReview(reviewId: Long, tag: ReviewTag): Result<ReviewTag> = try {
        val dto = reviewTagMapper.mapToDto(tag).copy(reviewId = reviewId)
        remoteDataSource.addReviewTag(dto).map { reviewTagMapper.mapToDomain(it) }
    } catch (e: IOException) {
        Log.e(TAG, "Failed to add tag to review: $reviewId", e)
        Result.failure(DomainError.NetworkError("Network error adding tag", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in addTagToReview", e)
        Result.failure(DomainError.UnknownError("Unknown error adding tag", e))
    }

    override suspend fun removeTag(tagId: Long): Result<Unit> = try {
        remoteDataSource.removeReviewTag(tagId)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to remove tag: $tagId", e)
        Result.failure(DomainError.NetworkError("Network error removing tag", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in removeTag", e)
        Result.failure(DomainError.UnknownError("Unknown error removing tag", e))
    }
}
