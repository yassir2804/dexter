package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.mapper.ReviewMapper
import com.dexter.app.domain.model.Review
import com.dexter.app.domain.repository.ReviewRepository
import java.io.IOException

class ReviewRepositoryImpl(
    private val reviewMapper: ReviewMapper
) : ReviewRepository {

    companion object { private const val TAG = "ReviewRepository" }

    override suspend fun getReviewById(id: Long): Review? = try {
        null
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get review by id: $id", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getReviewById", e)
        null
    }

    override suspend fun getReviewsForSession(sessionId: Long): List<Review> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get reviews for session: $sessionId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getReviewsForSession", e)
        emptyList()
    }

    override suspend fun getReviewsForUser(userId: Long): List<Review> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get reviews for user: $userId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getReviewsForUser", e)
        emptyList()
    }

    override suspend fun createReview(review: Review): Review = try {
        review
    } catch (e: IOException) {
        Log.e(TAG, "Failed to create review", e)
        review
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in createReview", e)
        review
    }
}
