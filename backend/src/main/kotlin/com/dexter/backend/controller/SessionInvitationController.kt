package com.dexter.backend.controller

import com.dexter.backend.dto.SessionInvitationDetails
import com.dexter.backend.dto.SessionInvitationInput
import com.dexter.backend.service.SessionInvitationService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * SessionInvitations (SessionInviteScreen).
 *
 *  GET    /v1/session-invitations?receiverId=    — invitaciones recibidas
 *  GET    /v1/session-invitations/{id}            — detalle
 *  POST   /v1/session-invitations                 — crear invitación
 *  PATCH  /v1/session-invitations/{id}/accept     — aceptar (status=ACCEPTED)
 *  PATCH  /v1/session-invitations/{id}/reject     — rechazar (status=REJECTED)
 *  PATCH  /v1/session-invitations/{id}/cancel     — cancelar (status=EXPIRED)
 */
@RestController
@RequestMapping("\${url.sessionInvitations}")
class SessionInvitationController(
    private val invitationService: SessionInvitationService,
) {

    @GetMapping
    fun findByReceiver(@RequestParam receiverId: Long): List<SessionInvitationDetails> =
        invitationService.findByReceiver(receiverId)

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): SessionInvitationDetails =
        invitationService.findById(id)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody request: SessionInvitationInput): SessionInvitationDetails =
        invitationService.create(request)

    @PatchMapping("/{id}/accept")
    fun accept(@PathVariable id: Long): SessionInvitationDetails =
        invitationService.accept(id)

    @PatchMapping("/{id}/reject")
    fun reject(@PathVariable id: Long): SessionInvitationDetails =
        invitationService.reject(id)

    @PatchMapping("/{id}/cancel")
    fun cancel(@PathVariable id: Long): SessionInvitationDetails =
        invitationService.cancel(id)
}
