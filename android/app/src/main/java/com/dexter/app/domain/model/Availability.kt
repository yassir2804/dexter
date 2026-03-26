package com.dexter.app.domain.model

/**
 * Simple representation of a user's weekly availability.
 *
 * For the course this stores day and time as strings (easy to read). In production
 * consider using proper time types (`LocalTime` / `Instant`) and validate ranges.
 *
 * @property id Availability record id.
 * @property userId Owner user id.
 * @property dayOfWeek Day name or code (e.g. "Mon").
 * @property startTime Start time (formatted string).
 * @property endTime End time (formatted string).
 */
data class Availability(
    val id: Long,
    val userId: Long,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String
)
