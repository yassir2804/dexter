package com.dexter.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * TODO (Fase 8+): notificaciones del user (NotificationsScreen).
 * Requerirá push (FCM) o polling — se evalúa en Fase 8+.
 * Entidad Notification ya existe en entity/Notification.kt desde Fase 2.
 */
@RestController
@RequestMapping("\${url.notifications}")
class NotificationController {

    @GetMapping
    fun findAll(): Nothing = TODO("Fase 8+: listar notificaciones del user ordenadas por fecha desc")

    @PatchMapping("/{id}/read")
    fun markAsRead(@PathVariable id: Long): Nothing = TODO("Fase 8+: marcar notificación como leída")
}
