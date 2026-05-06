package com.dexter.backend.controller

import com.dexter.backend.dto.SessionDetails
import com.dexter.backend.dto.SessionRequest
import com.dexter.backend.service.SessionService
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
 * Sessions (PlanScreen / EditSessionScreen / ActiveSessionScreen).
 *
 *  GET    /v1/sessions?matchId=    — sesiones de un match (ordenadas por sessionNumber)
 *  GET    /v1/sessions/{id}        — detalle
 *  POST   /v1/sessions             — crear
 *  PUT    /v1/sessions/{id}        — actualizar (título, descripción, status, fechas)
 *  DELETE /v1/sessions/{id}        — borrar
 */
@RestController
@RequestMapping("\${url.sessions}")
class SessionController(
    private val sessionService: SessionService,
) {

    @GetMapping
    fun findByMatch(@RequestParam matchId: Long): List<SessionDetails> =
        sessionService.findByMatch(matchId)

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): SessionDetails = sessionService.findById(id)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody request: SessionRequest): SessionDetails =
        sessionService.create(request)

    @PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@PathVariable id: Long, @RequestBody request: SessionRequest): SessionDetails =
        sessionService.update(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = sessionService.delete(id)
}
