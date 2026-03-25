package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Availability

/** Repository contract for user availability (time slots). */
interface AvailabilityRepository {
    suspend fun getAvailabilityForUser(userId: Long): List<Availability>
    suspend fun getAvailabilityById(id: Long): Availability?
    suspend fun saveAvailability(availability: Availability): Availability
    suspend fun deleteAvailability(id: Long)
}
