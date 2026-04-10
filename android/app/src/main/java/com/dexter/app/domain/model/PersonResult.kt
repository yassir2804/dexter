package com.dexter.app.domain.model

/**
 * A person who offers a specific skill, shown in search results.
 * Includes AI compatibility scoring for sorting and display.
 */
data class PersonResult(
    val userId: Long,
    val name: String,
    val avatarUrl: String?,
    val initials: String,
    val skillDescription: String,
    val rating: Double,
    val sessionCount: Int,
    val availability: String,
    val aiScore: Int,
    val aiReason: String
)
