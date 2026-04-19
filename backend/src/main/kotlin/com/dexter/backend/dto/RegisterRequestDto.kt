package com.dexter.backend.dto

/**
 * Recibido en POST /v1/auth/register. Coincide campo a campo con
 * RegisterRequestDto del frontend (data/datasource/model/RegisterRequestDto.kt).
 */
data class RegisterRequestDto(
    var email: String? = null,
    var password: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var country: String? = null,
    var skillsToTeach: List<String> = emptyList(),
    var skillsToLearn: List<String> = emptyList(),
)
