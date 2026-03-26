package com.dexter.app.data.datasource.model

/** DTO for Review. */
data class ReviewDto(
    val id: Long? = null,
    val reviewerId: Long? = null,
    val revieweeId: Long? = null,
    val sessionId: Long? = null,
    val rating: Int? = null,
    val comment: String? = null,
    val createdAt: Long? = null
)
