package com.dexter.app.domain.model

/**
 * Join table model linking roles to privileges.
 *
 * @property roleId Role identifier.
 * @property privilegeId Privilege identifier.
 */
data class RolePrivilege(
    val roleId: Long,
    val privilegeId: Long
)
