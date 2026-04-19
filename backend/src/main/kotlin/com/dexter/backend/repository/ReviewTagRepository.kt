package com.dexter.backend.repository

import com.dexter.backend.entity.ReviewTag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewTagRepository : JpaRepository<ReviewTag, Long> {
    fun findByReviewId(reviewId: Long): List<ReviewTag>
}
