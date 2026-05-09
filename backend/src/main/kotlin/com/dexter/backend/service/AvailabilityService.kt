package com.dexter.backend.service

import com.dexter.backend.dto.AvailabilityDetails
import com.dexter.backend.dto.AvailabilityRequest

interface AvailabilityService {
    fun findByUser(userId: Long): List<AvailabilityDetails>
    fun findById(id: Long): AvailabilityDetails
    fun create(request: AvailabilityRequest): AvailabilityDetails
    fun update(id: Long, request: AvailabilityRequest): AvailabilityDetails
    fun delete(id: Long)
}
