package com.dexter.backend.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * TODO (Fase 8+): CRUD de Session.
 * Depende de: IA service para generar el plan de 5 sesiones automáticamente.
 * Entidad Session ya existe en entity/Session.kt desde Fase 2.
 */
@RestController
@RequestMapping("\${url.sessions}")
class SessionController {

    @GetMapping
    fun findAll(): Nothing = TODO("Fase 8+: listar sesiones por match o por user")

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): Nothing = TODO("Fase 8+")

    @PostMapping
    fun create(): Nothing = TODO("Fase 8+: crear sesión individual o plan de 5 (IA)")

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long): Nothing = TODO("Fase 8+: reprogramar o marcar como completada")

    @DeleteMapping("/{id}")
    fun cancel(@PathVariable id: Long): Nothing = TODO("Fase 8+: cancelar sesión")
}
