package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Role

/** Repository contract for roles (RBAC). */
interface RoleRepository {
    suspend fun getRoleById(id: Long): Role?
    suspend fun getAllRoles(): List<Role>
    suspend fun saveRole(role: Role): Role
    suspend fun deleteRole(id: Long)
}
