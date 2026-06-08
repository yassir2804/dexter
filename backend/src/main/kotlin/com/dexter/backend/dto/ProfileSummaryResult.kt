package com.dexter.backend.dto

/**
 * Perfil completo de un usuario: datos básicos + skills + reviews.
 *
 * Respuesta de GET /v1/profile-summary?userId= y body de PUT /v1/profile-summary.
 * Coincide campo a campo con el DTO del front
 * (data/datasource/model/ProfileSummaryDto.kt).
 */
data class ProfileSummaryResult(
    var initials: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var avatarUrl: String? = null,
    var bio: String? = null,
    var country: String? = null,
    var rating: Double? = null,
    var totalSessions: Int? = null,
    var totalMatches: Int? = null,
    var profileVisible: Boolean? = null,
    var phone: String? = null,
    var updateFirstName: String? = null,
    var updateLastName: String? = null,
    var updateTotalSessions: Int? = null,
    var updateRating: Double? = null,
    /** nombres de Skill donde skillRole = OFFERED */
    var skillsOffered: List<String>? = null,
    /** nombres de Skill donde skillRole = SOUGHT */
    var skillsSought: List<String>? = null,
    var reviews: List<ProfileReviewItemDetails>? = null,
    var availabilityDays: List<Int>? = null,
)
