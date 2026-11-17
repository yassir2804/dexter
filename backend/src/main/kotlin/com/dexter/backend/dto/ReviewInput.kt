package com.dexter.backend.dto

/**
 * Body para POST /v1/reviews — el front lo envía cuando un user califica una sesión.
 */
data class ReviewInput(
    var reviewerId: Long? = null,
    var revieweeId: Long? = null,
    var sessionId: Long? = null,
    var rating: Int? = null,
    var comment: String? = null,
    var tags: List<String> = emptyList(),
)
