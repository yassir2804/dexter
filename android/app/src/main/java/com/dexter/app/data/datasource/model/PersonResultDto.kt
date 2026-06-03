package com.dexter.app.data.datasource.model

/**
 * DTO for a person in search results returned by the API.
 * Maps directly to the JSON from GET /search/people?skill=...
 */
data class PersonResultDto(
    val userId: Long?,
    val name: String?,
    val avatarUrl: String?,
    val initials: String?,
    val skillDescription: String?,
    val rating: Double?,
    val sessionCount: Int?,
    val availability: String?,
    val aiScore: Int?,
    val aiReason: String?
)
