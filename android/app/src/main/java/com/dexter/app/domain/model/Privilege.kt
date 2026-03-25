package com.dexter.app.domain.model

/**
 * Privilege used by roles (RBAC).
 *
 * @property id Privilege identifier.
 * @property name Privilege name.
 */
data class Privilege(
    val id: Long,
    val name: String
)
