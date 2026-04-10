package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.UserDto
import com.dexter.app.domain.model.User
import java.util.Date
import javax.inject.Inject

/**
 * Mapper for converting between `UserDto` (wire) and `User` (domain).
 *
 * Kept in a dedicated `mappers` package to separate transformation logic
 * from DTO/data model declarations. Style follows TaskApp frontend comments:
 * concise KDoc and safe, conservative mappings.
 */
class UserMapper @Inject constructor() {

    /**
     * Maps a `UserDto` to domain `User`.
     * @param dto the data object to convert
     * @return domain `User`
     */
    fun mapToDomain(dto: UserDto): User = User(
        id = dto.id ?: 0L,
        email = dto.email ?: "",
        password = dto.password,
        firstName = dto.firstName ?: "",
        lastName = dto.lastName ?: "",
        username = dto.username ?: "",
        bio = dto.bio,
        avatarUrl = dto.avatarUrl,
        country = dto.country,
        enabled = dto.enabled ?: true,
        tokenExpired = dto.tokenExpired ?: false,
        rating = dto.rating ?: 0.0,
        totalSessions = dto.totalSessions ?: 0,
        totalMatches = dto.totalMatches ?: 0,
        createDate = Date(dto.createDate ?: 0L),
        updatedAt = Date(dto.updatedAt ?: 0L)
    )

    /**
     * Maps domain `User` to `UserDto`.
     * @param domain the domain object to convert
     * @return `UserDto` for data layer
     */
    fun mapToDto(domain: User): UserDto = UserDto(
        id = domain.id,
        email = domain.email,
        password = domain.password,
        firstName = domain.firstName,
        lastName = domain.lastName,
        username = domain.username,
        bio = domain.bio,
        avatarUrl = domain.avatarUrl,
        country = domain.country,
        enabled = domain.enabled,
        tokenExpired = domain.tokenExpired,
        rating = domain.rating,
        totalSessions = domain.totalSessions,
        totalMatches = domain.totalMatches,
        createDate = domain.createDate.time,
        updatedAt = domain.updatedAt.time
    )
}