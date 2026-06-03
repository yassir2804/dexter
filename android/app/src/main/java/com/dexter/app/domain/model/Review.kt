package com.dexter.app.domain.model

import java.util.Date

/**
 * Review left by a user for a completed session.
 *
 * @property id Review identifier.
 * @property reviewerId User who wrote the review.
 * @property revieweeId User who received the review.
 * @property sessionId Related session id.
 * @property rating Numeric rating (1-5).
 * @property comment Optional textual feedback.
 * @property createdAt Creation timestamp.
 */
data class Review(
    val id: Long,
    val reviewerId: Long,
    val revieweeId: Long,
    val sessionId: Long,
    val rating: Int,
    val comment: String?,
    val createdAt: Date,
    val tags: List<String> = emptyList(),
)
