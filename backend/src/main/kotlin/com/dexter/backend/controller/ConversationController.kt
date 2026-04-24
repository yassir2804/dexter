package com.dexter.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * TODO (Fase 8+): conversaciones del user (MessagesScreen).
 * Entidad Conversation ya existe en entity/Conversation.kt desde Fase 2.
 */
@RestController
@RequestMapping("\${url.conversations}")
class ConversationController {

    @GetMapping
    fun findAll(): Nothing = TODO("Fase 8+: listar conversaciones del user actual con last message")

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): Nothing = TODO("Fase 8+: detalle de conversación")
}
