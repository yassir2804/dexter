package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Privilege

/** Repository contract for role-privilege assignments. */
interface RolePrivilegeRepository {
    suspend fun getPrivilegesForRole(roleId: Long): List<Privilege>
    suspend fun addPrivilegeToRole(roleId: Long, privilegeId: Long)
    suspend fun removePrivilegeFromRole(roleId: Long, privilegeId: Long)
}
