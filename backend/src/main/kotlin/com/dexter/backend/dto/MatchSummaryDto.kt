package com.dexter.backend.dto

/**
 * Proyección compacta de Match para las tarjetas del Home.
 * Todos los datos son "del otro usuario" (no el currentUser).
 * Coincide campo a campo con MatchSummaryDto del front
 * (data/datasource/model/MatchSummaryDto.kt).
 */
data class MatchSummaryDto(
    var initials: String? = null,
    var name: String? = null,
    var avatarUrl: String? = null,
    /** Skills que el otro usuario OFFERED, concatenadas con ", " */
    var offers: String? = null,
    /** Skills que el otro usuario SOUGHT, concatenadas con ", " */
    var seeks: String? = null,
    /** Rating formateado como string (ej "4.8") */
    var rating: String? = null,
    var sessions: Int? = null,
    /** Porcentaje como string (ej "92%") */
    var compatibilityPercent: String? = null,
)
