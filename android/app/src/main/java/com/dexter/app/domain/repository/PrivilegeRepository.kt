package com.dexter.app.domain.repository

import com.dexter.app.domain.model.Privilege

/** Repository contract for privileges (RBAC). */
interface PrivilegeRepository {
    suspend fun getPrivilegeById(id: Long): Privilege?
    suspend fun getAllPrivileges(): List<Privilege>
    suspend fun savePrivilege(privilege: Privilege): Privilege
    suspend fun deletePrivilege(id: Long)
}
