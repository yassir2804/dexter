package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.datasource.ReviewDataSource
import com.dexter.app.data.mapper.ReviewMapper
import com.dexter.app.data.remote.ReviewRemoteDataSource
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.Review
import com.dexter.app.domain.repository.ReviewRepository
import java.io.IOException
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    @Suppress("unused") private val dataSource: ReviewDataSource,
    private val remoteDataSource: ReviewRemoteDataSource,
    private val reviewMapper: ReviewMapper
) : ReviewRepository {

    companion object { private const val TAG = "ReviewRepository" }

    override suspend fun getReviewById(id: Long): Result<Review> = try {
        remoteDataSource.getReviews().fold(
            onSuccess = { dtos ->
                val dto = dtos.firstOrNull { it.id == id }
                if (dto != null) {
                    Result.success(reviewMapper.mapToDomain(dto))
                } else {
                    Result.failure(DomainError.ReviewError("Review not found"))
                }
            },
            onFailure = { exception ->
                Result.failure(DomainError.NetworkError("Network error fetching review $id", exception))
            }
        )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get review by id: $id", e)
        Result.failure(DomainError.NetworkError("Network error fetching review $id", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getReviewById", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching review $id", e))
    }

    override suspend fun getReviewsForSession(sessionId: Long): Result<List<Review>> = try {
        remoteDataSource.getReviews().fold(
            onSuccess = { dtos ->
                Result.success(
                    dtos
                        .filter { it.sessionId == sessionId }
                        .map { reviewMapper.mapToDomain(it) }
                )
            },
            onFailure = { exception ->
                Result.failure(
                    DomainError.NetworkError("Network error fetching session reviews", exception)
                )
            }
        )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get reviews for session: $sessionId", e)
        Result.failure(DomainError.NetworkError("Network error fetching session reviews", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getReviewsForSession", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching session reviews", e))
    }

    override suspend fun getReviewsForUser(userId: Long): Result<List<Review>> = try {
        remoteDataSource.getReviews().fold(
            onSuccess = { dtos ->
                Result.success(
                    dtos
                        .filter { it.revieweeId == userId }
                        .map { reviewMapper.mapToDomain(it) }
                )
            },
            onFailure = { exception ->
                Result.failure(
                    DomainError.NetworkError("Network error fetching user reviews", exception)
                )
            }
        )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get reviews for user: $userId", e)
        Result.failure(DomainError.NetworkError("Network error fetching user reviews", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getReviewsForUser", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching user reviews", e))
    }

    override suspend fun createReview(review: Review): Result<Review> = try {
        remoteDataSource.createReview(reviewMapper.mapToDto(review)).fold(
            onSuccess = { dto -> Result.success(reviewMapper.mapToDomain(dto)) },
            onFailure = { exception ->
                Result.failure(DomainError.NetworkError("Network error creating review", exception))
            }
        )
    } catch (e: IOException) {
        Log.e(TAG, "Failed to create review", e)
        Result.failure(DomainError.NetworkError("Network error creating review", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in createReview", e)
        Result.failure(DomainError.UnknownError("Unknown error creating review", e))
    }
}
