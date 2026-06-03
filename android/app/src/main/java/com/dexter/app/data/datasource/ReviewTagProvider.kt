package com.dexter.app.data.datasource

import com.dexter.app.domain.model.ReviewTag

/**
 * In-memory catalogue of the tags users can attach to a review.
 * The IDs are stable and match the order shown in the UI.
 */
class ReviewTagProvider {
    companion object {
        private val availableTags = listOf(
            ReviewTag(id = 1, reviewId = 0, tagName = "Puntual"),
            ReviewTag(id = 2, reviewId = 0, tagName = "Buen profesor"),
            ReviewTag(id = 3, reviewId = 0, tagName = "Paciente"),
            ReviewTag(id = 4, reviewId = 0, tagName = "Metodología clara"),
            ReviewTag(id = 5, reviewId = 0, tagName = "Motivador")
        )

        fun findAvailableTags(): List<ReviewTag> = availableTags
    }
}
