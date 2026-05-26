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

    override suspend fun getCatalog(): Result<List<String>> = try {
        remoteDataSource.getCatalog()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to load review-tag catalog", e)
        Result.failure(DomainError.NetworkError("Network error fetching tag catalog", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getCatalog", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching tag catalog", e))
    }

    override suspend fun getTagsForReview(reviewId: Long): Result<List<ReviewTag>> = try {
        remoteDataSource.getTagsForReview(reviewId).map { dtos ->
            dtos.map { reviewTagMapper.mapToDomain(it) }
        }
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get tags for review: $reviewId", e)
        Result.failure(DomainError.NetworkError("Network error fetching review tags", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getTagsForReview", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching review tags", e))
    }
}
