package com.dexter.backend.repository

import com.dexter.backend.entity.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByRevieweeIdOrderByCreatedAtDesc(revieweeId: Long): List<Review>
    fun findBySessionId(sessionId: Long): List<Review>
}
