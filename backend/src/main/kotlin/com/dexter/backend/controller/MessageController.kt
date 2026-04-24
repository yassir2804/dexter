package com.dexter.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * TODO (Fase 8+): mensajes dentro de una conversación (ChatScreen).
 * Se evalúa WebSocket/SSE para tiempo real en Fase 8+.
 * Entidad Message ya existe en entity/Message.kt desde Fase 2.
 */
@RestController
@RequestMapping("\${url.conversations}/{conversationId}/messages")
class MessageController {

    @GetMapping
    fun findByConversation(@PathVariable conversationId: Long): Nothing =
        TODO("Fase 8+: mensajes paginados de una conversación")

    @PostMapping
    fun create(@PathVariable conversationId: Long): Nothing =
        TODO("Fase 8+: enviar mensaje (HTTP) o vía WebSocket en tiempo real")
}
