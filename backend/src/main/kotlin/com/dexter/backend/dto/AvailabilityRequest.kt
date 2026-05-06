package com.dexter.backend.dto

data class AvailabilityRequest(
    var userId: Long? = null,
    /** 1 = Monday ... 7 = Sunday (ISO-8601). */
    var dayOfWeek: Int? = null,
    /** Formato HH:mm (24h). */
    var startTime: String? = null,
    /** Formato HH:mm (24h). */
    var endTime: String? = null,
)
