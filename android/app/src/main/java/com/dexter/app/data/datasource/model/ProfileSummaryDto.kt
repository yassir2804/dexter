package com.dexter.app.data.datasource.model

/** DTO for ProfileSummary — the format data would arrive from an API. */
data class ProfileSummaryDto(
    val initials: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val country: String? = null,
    val rating: Double? = null,
    val totalSessions: Int? = null,
    val totalMatches: Int? = null,
    val profileVisible: Boolean? = null,
    val skillsOffered: List<String>? = null,
    val skillsSought: List<String>? = null,
    val reviews: List<ProfileReviewItemDto>? = null
)

data class ProfileReviewItemDto(
    val reviewerName: String? = null,
    val reviewerAvatarUrl: String? = null,
    val rating: Int? = null,
    val comment: String? = null
)
