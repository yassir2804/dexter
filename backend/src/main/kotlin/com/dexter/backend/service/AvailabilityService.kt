package com.dexter.backend.service

import com.dexter.backend.dto.AvailabilityDetails
import com.dexter.backend.dto.AvailabilityInput

interface AvailabilityService {
    fun findByUser(userId: Long): List<AvailabilityDetails>
    fun findById(id: Long): AvailabilityDetails
    fun create(request: AvailabilityInput): AvailabilityDetails
    fun update(id: Long, request: AvailabilityInput): AvailabilityDetails
    fun delete(id: Long)
}
