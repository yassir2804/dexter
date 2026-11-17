package com.dexter.backend.controller

import com.dexter.backend.dto.ReviewTagDetails
import com.dexter.backend.repository.ReviewTagRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * ReviewTags — tags asociados a reviews (Buen profesor, Paciente, etc).
 *
 *  GET /v1/review-tags?reviewId=    — tags de un review específico
 *  GET /v1/review-tags/catalog      — catálogo sugerido para el picker del front
 */
@RestController
@RequestMapping("\${url.reviewTags}")
class ReviewTagController(
    private val reviewTagRepository: ReviewTagRepository,
) {

    @GetMapping
    fun findByReview(@RequestParam reviewId: Long): List<ReviewTagDetails> =
        reviewTagRepository.findByReviewId(reviewId).map { t ->
            ReviewTagDetails(
                id = t.id,
                reviewId = t.review?.id,
                tagName = t.tagName,
            )
        }

    /**
     * Catalogo fijo de tags sugeridos para el picker del cliente.
     * No se guardan como entidad — son strings que el cliente envía
     * al crear el review en `tags`.
     */
    @GetMapping("/catalog")
    fun catalog(): List<String> = listOf(
        "Paciente",
        "Buen profesor",
        "Claro",
        "Puntual",
        "Amable",
        "Bien preparado",
        "Motivador",
        "Practico",
        "Profesional",
        "Recomendado",
    )
}
