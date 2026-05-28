package com.dexter.backend.dto

/**
 * Respuesta de POST /v1/auth/register y GET /v1/users/...
 * Coincide campo a campo con UserDto del frontend
 * (data/datasource/model/UserDto.kt).
 *
 * El password NUNCA se expone en la API: por eso esta ausente
 * del DTO. El cliente no necesita ese campo, y filtrarlo aqui
 * evita que se filtre por accidente desde el UserMapper.
 */
data class UserResult(
    var id: Long? = null,
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var username: String? = null,
    var bio: String? = null,
    var avatarUrl: String? = null,
    var country: String? = null,
    var enabled: Boolean? = null,
    var profileVisible: Boolean? = null,
    var tokenExpired: Boolean? = null,
    var rating: Double? = null,
    var totalSessions: Int? = null,
    var totalMatches: Int? = null,
    /** Epoch millis for created date */
    var createDate: Long? = null,
    /** Epoch millis for last updated date */
    var updatedAt: Long? = null,
)
