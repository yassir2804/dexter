package com.dexter.app.domain.model

import java.util.Date

/**
 * Domain model representing an application user and profile.
 *
 * @property id Unique identifier for the user.
 * @property email Email address (used for auth/communication).
 * @property password Nullable password field (may not be present in all flows).
 * @property firstName User's first name.
 * @property lastName User's last name.
 * @property username Public display name / handle.
 * @property bio Short profile description (optional).
 * @property avatarUrl Optional avatar image URL.
 * @property country Optional user country.
 * @property enabled Whether the user account is active.
 * @property tokenExpired Whether the authentication token is expired.
 * @property rating Aggregate user rating (e.g. 1.0-5.0).
 * @property totalSessions Number of completed sessions.
 * @property totalMatches Number of matches created.
 * @property createDate Creation timestamp.
 * @property updatedAt Last update timestamp.
 */
data class User(
    val id: Long,
    val email: String,
    val password: String?,
    val firstName: String,
    val lastName: String,
    val username: String,
    val bio: String?,
    val avatarUrl: String?,
    val country: String?,
    val enabled: Boolean,
    val profileVisible: Boolean = true,
    val tokenExpired: Boolean,
    val rating: Double,
    val totalSessions: Int,
    val totalMatches: Int,
    val createDate: Date,
    val updatedAt: Date,
    val phone: String? = null,
    val skillsToTeach: List<String> = emptyList(),
    val skillsToLearn: List<String> = emptyList(),
    val availabilityDays: List<Int> = emptyList(),  // ISO-8601: 1=Mon, 7=Sun
    val roles: List<String> = emptyList(),
)
