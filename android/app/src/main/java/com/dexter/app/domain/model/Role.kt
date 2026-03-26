package com.dexter.app.domain.model

/**
 * Security role (RBAC).
 *
 * @property id Role identifier.
 * @property name Role name (e.g. "ADMIN", "USER").
 */
data class Role(
    val id: Long,
    val name: String
)
