package com.dexter.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * TODO (Fase 8+): CRUD de reviews (RateScreen).
 * Lectura parcial ya se expone desde ProfileService (últimas 10 reviews del user).
 * Entidad Review ya existe en entity/Review.kt desde Fase 2.
 */
@RestController
@RequestMapping("\${url.reviews}")
class ReviewController {

    @GetMapping
    fun findAll(@RequestParam(required = false) userId: Long?): Nothing =
        TODO("Fase 8+: listar reviews, opcionalmente filtrar por reviewee")

    @PostMapping
    fun create(): Nothing =
        TODO("Fase 8+: crear review post-sesión (requiere session COMPLETED)")
}
