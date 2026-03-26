package com.dexter.app.domain.model

/**
 * Join table model linking users to roles.
 *
 * @property userId User identifier.
 * @property roleId Role identifier.
 */
data class UserRole(
    val userId: Long,
    val roleId: Long
)
