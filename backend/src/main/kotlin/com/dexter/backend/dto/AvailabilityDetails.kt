package com.dexter.backend.dto

data class AvailabilityDetails(
    var id: Long? = null,
    var userId: Long? = null,
    var dayOfWeek: Int? = null,
    var startTime: String? = null,
    var endTime: String? = null,
)
