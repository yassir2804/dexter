package com.dexter.backend.dto

/**
 * Objeto completo de Match (Match Detail del front).
 * Coincide campo a campo con MatchDto del front
 * (data/datasource/model/MatchDto.kt).
 */
data class MatchDto(
    var id: Long? = null,
    var initiatorId: Long? = null,
    var receiverId: Long? = null,
    var compatibilityScore: Double? = null,
    /** Nombre del enum MatchStatus (PENDING | ACCEPTED | REJECTED | CANCELLED) */
    var status: String? = null,
    /** Epoch millis */
    var createdAt: Long? = null,
)
