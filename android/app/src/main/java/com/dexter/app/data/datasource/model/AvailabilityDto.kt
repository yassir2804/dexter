package com.dexter.app.data.datasource.model

/** DTO for Availability entries. */
data class AvailabilityDto(
    val id: Long? = null,
    val userId: Long? = null,
    val dayOfWeek: String? = null,
    val startTime: String? = null,
    val endTime: String? = null
)
