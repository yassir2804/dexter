package com.dexter.backend.dto

/**
 * Resultado de búsqueda de personas que ofrecen una skill.
 * Coincide campo a campo con PersonResultDto del front
 * (data/datasource/model/PersonResultDto.kt).
 */
data class PersonResult(
    var userId: Long? = null,
    var name: String? = null,
    var avatarUrl: String? = null,
    var initials: String? = null,
    /** Descripción libre (ej "Guitarra — nivel advanced") */
    var skillDescription: String? = null,
    var rating: Double? = null,
    var sessionCount: Int? = null,
    /** Disponibilidad como string (placeholder hasta Availability completa) */
    var availability: String? = null,
    /** Score IA 0-100 (null por ahora, se completa en fases futuras) */
    var aiScore: Int? = null,
    var aiReason: String? = null,
)
