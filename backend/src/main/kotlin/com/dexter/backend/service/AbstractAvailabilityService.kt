package com.dexter.backend.service

import com.dexter.backend.dto.AvailabilityDetails
import com.dexter.backend.dto.AvailabilityInput
import com.dexter.backend.entity.Availability
import com.dexter.backend.repository.AvailabilityRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class AbstractAvailabilityService(
    @Autowired val availabilityRepository: AvailabilityRepository,
    @Autowired val userRepository: UserRepository,
) : AvailabilityService {

    override fun findByUser(userId: Long): List<AvailabilityDetails> =
        availabilityRepository.findByUserId(userId).map(::toDetails)

    override fun findById(id: Long): AvailabilityDetails =
        toDetails(availabilityRepository.findById(id).orElseThrow {
            NoSuchElementException("Availability with id $id not found")
        })

    override fun create(request: AvailabilityInput): AvailabilityDetails {
        val userId = request.userId ?: throw IllegalArgumentException("userId is required")
        val dayOfWeek = request.dayOfWeek ?: throw IllegalArgumentException("dayOfWeek is required")
        if (dayOfWeek !in 1..7) {
            throw IllegalArgumentException("dayOfWeek must be 1..7 (1=Monday)")
        }
        val startTime = parseTime(request.startTime, "startTime")
        val endTime = parseTime(request.endTime, "endTime")
        if (!endTime.isAfter(startTime)) {
            throw IllegalArgumentException("endTime must be after startTime")
        }

        val user = userRepository.findById(userId).orElseThrow {
            NoSuchElementException("User with id $userId not found")
        }

        val saved = availabilityRepository.save(
            Availability(
                user = user,
                dayOfWeek = dayOfWeek,
                startTime = startTime,
                endTime = endTime,
            )
        )
        return toDetails(saved)
    }

    override fun update(id: Long, request: AvailabilityInput): AvailabilityDetails {
        val a = availabilityRepository.findById(id).orElseThrow {
            NoSuchElementException("Availability with id $id not found")
        }
        request.dayOfWeek?.let {
            if (it !in 1..7) throw IllegalArgumentException("dayOfWeek must be 1..7")
            a.dayOfWeek = it
        }
        request.startTime?.let { a.startTime = parseTime(it, "startTime") }
        request.endTime?.let { a.endTime = parseTime(it, "endTime") }
        if (!a.endTime.isAfter(a.startTime)) {
            throw IllegalArgumentException("endTime must be after startTime")
        }
        return toDetails(availabilityRepository.save(a))
    }

    override fun delete(id: Long) {
        if (!availabilityRepository.existsById(id)) {
            throw NoSuchElementException("Availability with id $id not found")
        }
        availabilityRepository.deleteById(id)
    }

    private fun parseTime(s: String?, fieldName: String): LocalTime {
        if (s.isNullOrBlank()) throw IllegalArgumentException("$fieldName is required")
        return try {
            LocalTime.parse(s)
        } catch (e: Exception) {
            throw IllegalArgumentException("$fieldName must be HH:mm format (got '$s')")
        }
    }

    private fun toDetails(a: Availability): AvailabilityDetails = AvailabilityDetails(
        id = a.id,
        userId = a.user?.id,
        dayOfWeek = a.dayOfWeek,
        startTime = a.startTime.toString(),
        endTime = a.endTime.toString(),
    )
}
