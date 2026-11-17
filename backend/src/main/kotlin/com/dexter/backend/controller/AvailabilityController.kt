package com.dexter.backend.controller

import com.dexter.backend.dto.AvailabilityDetails
import com.dexter.backend.dto.AvailabilityInput
import com.dexter.backend.service.AvailabilityService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Availability — disponibilidad horaria del user para matching por horario.
 *
 *  GET    /v1/availability?userId=    — lista del user
 *  GET    /v1/availability/{id}        — detalle
 *  POST   /v1/availability             — crear franja (HH:mm)
 *  PUT    /v1/availability/{id}        — modificar
 *  DELETE /v1/availability/{id}        — borrar
 */
@RestController
@RequestMapping("\${url.availability}")
class AvailabilityController(
    private val availabilityService: AvailabilityService,
) {

    @GetMapping
    fun findByUser(@RequestParam userId: Long): List<AvailabilityDetails> =
        availabilityService.findByUser(userId)

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): AvailabilityDetails =
        availabilityService.findById(id)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody request: AvailabilityInput): AvailabilityDetails =
        availabilityService.create(request)

    @PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@PathVariable id: Long, @RequestBody request: AvailabilityInput): AvailabilityDetails =
        availabilityService.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = availabilityService.delete(id)
}
