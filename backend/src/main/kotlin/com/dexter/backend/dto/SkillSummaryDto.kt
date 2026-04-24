package com.dexter.backend.dto

/**
 * Proyección de una skill agrupada (una entrada por nombre de skill).
 * Coincide campo a campo con SkillSummaryDto del front
 * (data/datasource/model/SkillSummaryDto.kt).
 */
data class SkillSummaryDto(
    var name: String? = null,
    var category: String? = null,
    /** Cantidad de usuarios distintos que tienen esa skill */
    var peopleCount: Int? = null,
    /** Icono libre; el front decide cómo renderizarlo (ej "music_note", "language", "code") */
    var iconType: String? = null,
)
