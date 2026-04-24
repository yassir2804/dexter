package com.dexter.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * TODO (Fase 8+): invitaciones a sesiones (enviar / aceptar / rechazar / expirar).
 * Entidad SessionInvitation ya existe en entity/SessionInvitation.kt desde Fase 2.
 */
@RestController
@RequestMapping("\${url.sessionInvitations}")
class SessionInvitationController {

    @GetMapping
    fun findAll(): Nothing = TODO("Fase 8+: listar invitaciones del user actual")

    @PostMapping
    fun create(): Nothing = TODO("Fase 8+: enviar invitación a otro user para una sesión")

    @PatchMapping("/{id}/accept")
    fun accept(@PathVariable id: Long): Nothing = TODO("Fase 8+: aceptar invitación → crea Session")

    @PatchMapping("/{id}/reject")
    fun reject(@PathVariable id: Long): Nothing = TODO("Fase 8+: rechazar invitación")
}
