package com.dexter.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * TODO (Fase 8+): tags disponibles para reviews (Puntual, Buen profesor, Paciente, etc).
 * Entidad ReviewTag ya existe en entity/ReviewTag.kt desde Fase 2.
 */
@RestController
@RequestMapping("\${url.reviewTags}")
class ReviewTagController {

    @GetMapping
    fun findAll(): Nothing = TODO("Fase 8+: listar tags disponibles para evaluar una sesión")
}
