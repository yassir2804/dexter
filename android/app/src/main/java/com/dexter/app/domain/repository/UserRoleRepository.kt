package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Role

/** Repository contract for user-role assignments. */
interface UserRoleRepository {
    suspend fun getRolesForUser(userId: Long): List<Role>
    suspend fun addRoleToUser(userId: Long, roleId: Long)
    suspend fun removeRoleFromUser(userId: Long, roleId: Long)
}
