package com.dexter.backend.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * TODO (Fase 8+): disponibilidad horaria del user para mejorar matching.
 * Entidad Availability ya existe en entity/Availability.kt desde Fase 2.
 */
@RestController
@RequestMapping("\${url.availability}")
class AvailabilityController {

    @GetMapping
    fun findAll(): Nothing = TODO("Fase 8+: listar franjas horarias del user")

    @PostMapping
    fun create(): Nothing = TODO("Fase 8+: crear franja de disponibilidad")

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long): Nothing = TODO("Fase 8+: modificar franja")

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): Nothing = TODO("Fase 8+: eliminar franja")
}
