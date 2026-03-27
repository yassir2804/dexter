package com.dexter.app.data.datasource.model

/** DTO for MatchSummary — the format data would arrive from an API. */
data class MatchSummaryDto(
    val initials: String? = null,
    val name: String? = null,
    val avatarUrl: String? = null,
    val offers: String? = null,
    val seeks: String? = null,
    val rating: String? = null,
    val sessions: Int? = null,
    val compatibilityPercent: String? = null
)
