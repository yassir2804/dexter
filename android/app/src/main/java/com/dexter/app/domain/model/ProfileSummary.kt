package com.dexter.app.domain.model

/**
 * Enriched profile data ready for display.
 *
 * Combines data from users, skills, and reviews tables
 * into a single model that the ProfileScreen can show directly.
 */
data class ProfileSummary(
    val initials: String,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String?,
    val bio: String,
    val country: String?,
    val phone: String?,
    val rating: Double,
    val totalSessions: Int,
    val totalMatches: Int,
    val profileVisible: Boolean = true,
    val skillsOffered: List<String>,
    val skillsSought: List<String>,
    val reviews: List<ProfileReviewItem>
)

data class ProfileReviewItem(
    val reviewerName: String,
    val reviewerAvatarUrl: String?,
    val rating: Int,
    val comment: String
)
