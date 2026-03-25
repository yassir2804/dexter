package com.dexter.app.domain.model

/**
 * Tag applied to a review (short keyword, e.g. "clear", "patient").
 *
 * @property id Tag identifier.
 * @property reviewId Parent review id.
 * @property tagName Tag text.
 */
data class ReviewTag(
    val id: Long,
    val reviewId: Long,
    val tagName: String
)
