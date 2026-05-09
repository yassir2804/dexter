package com.dexter.backend.service

import com.dexter.backend.dto.ReviewDetails
import com.dexter.backend.dto.ReviewInput

/**
 * Operaciones sobre reviews (calificaciones post-sesión).
 */
interface ReviewService {
    fun findByReviewee(revieweeId: Long): List<ReviewDetails>
    fun findBySession(sessionId: Long): List<ReviewDetails>
    fun findById(id: Long): ReviewDetails
    fun create(request: ReviewInput): ReviewDetails
}
