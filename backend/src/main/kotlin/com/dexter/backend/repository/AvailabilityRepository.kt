package com.dexter.backend.repository

import com.dexter.backend.entity.Availability
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AvailabilityRepository : JpaRepository<Availability, Long> {
    fun findByUserId(userId: Long): List<Availability>
}
