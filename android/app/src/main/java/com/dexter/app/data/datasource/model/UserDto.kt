package com.dexter.app.data.datasource.model

/**
 * Data Transfer Object for user data coming from remote APIs or local storage.
 *
 * DTOs mirror the wire format (JSON) and are intentionally nullable to represent
 * partial responses. Mapping to the domain model must enforce required fields
 * and provide sensible defaults or fallbacks.
 */
data class UserDto(
    val id: Long? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null,
    val bio: String? = null,
    val avatarUrl: String? = null,
    val country: String? = null,
    val enabled: Boolean? = null,
    val profileVisible: Boolean? = null,
    val tokenExpired: Boolean? = null,
    val rating: Double? = null,
    val totalSessions: Int? = null,
    val totalMatches: Int? = null,
    /** Epoch millis for created date */
    val createDate: Long? = null,
    /** Epoch millis for last updated date */
    val updatedAt: Long? = null
)