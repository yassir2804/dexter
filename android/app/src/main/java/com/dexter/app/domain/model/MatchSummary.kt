package com.dexter.app.domain.model

/**
 * Enriched match data ready for display.
 *
 * Like Task in the TaskApp has priority.label and status.label,
 * MatchSummary has all the fields the HomeScreen needs to show a match card.
 */
data class MatchSummary(
    val initials: String,
    val name: String,
    val avatarUrl: String?,
    val offers: String,
    val seeks: String,
    val rating: String,
    val sessions: Int,
    val compatibilityPercent: String
)

