package com.dexter.backend.dto

/**
 * Item de review que aparece dentro de un ProfileSummary.
 * Coincide campo a campo con el DTO del front
 * (data/datasource/model/ProfileReviewItemDto.kt).
 */
data class ProfileReviewItemDetails(
    var reviewerName: String? = null,
    var reviewerAvatarUrl: String? = null,
    var rating: Int? = null,
    var comment: String? = null,
)
