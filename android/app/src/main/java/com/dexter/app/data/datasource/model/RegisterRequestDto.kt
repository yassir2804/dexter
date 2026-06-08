package com.dexter.app.data.datasource.model

/**
 * DTO sent as the JSON body of POST /auth/register.
 *
 * Gson serializes this to JSON automatically when Retrofit sends the request.
 * Only contains the fields the user fills in during registration
 * (not all User fields — the backend generates id, rating, dates, etc.).
 */
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val country: String,
    val phone: String?,
    val skillsToTeach: List<String>,
    val skillsToLearn: List<String>,
    val availabilityDays: List<Int>
)
