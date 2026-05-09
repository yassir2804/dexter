package com.dexter.backend.dto

/**
 * Respuesta de GET /v1/reviews — datos enriquecidos con nombre del reviewer.
 */
data class ReviewDetails(
    var id: Long? = null,
    var reviewerId: Long? = null,
    var reviewerName: String? = null,
    var reviewerAvatarUrl: String? = null,
    var revieweeId: Long? = null,
    var sessionId: Long? = null,
    var rating: Int? = null,
    var comment: String? = null,
    var createdAt: Long? = null,
)
