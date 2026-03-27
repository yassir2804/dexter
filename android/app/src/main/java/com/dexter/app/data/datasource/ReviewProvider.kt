package com.dexter.app.data.datasource

import com.dexter.app.domain.model.Review
import java.util.Date

/**
 * In-memory provider for sample Review data used during development.
 */
class ReviewProvider {
    companion object {
        private val reviewList = listOf(
            Review(
                id = 1,
                reviewerId = 2,
                revieweeId = 1,
                sessionId = 1,
                rating = 5,
                comment = "Excelente profesora de guitarra. Muy paciente y metodica.",
                createdAt = Date()
            ),
            Review(
                id = 2,
                reviewerId = 2,
                revieweeId = 1,
                sessionId = 2,
                rating = 4,
                comment = "Buenas sesiones de ingles. Explica con ejemplos practicos.",
                createdAt = Date()
            )
        )

        fun findReviewsByUserId(userId: Long): List<Review> =
            reviewList.filter { it.revieweeId == userId }

        fun findReviewById(id: Long): Review? =
            reviewList.find { it.id == id }
    }
}
