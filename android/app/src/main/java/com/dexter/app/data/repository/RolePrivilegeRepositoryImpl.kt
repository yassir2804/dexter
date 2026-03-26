package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.domain.model.Privilege
import com.dexter.app.domain.repository.RolePrivilegeRepository
import java.io.IOException

class RolePrivilegeRepositoryImpl : RolePrivilegeRepository {

    companion object { private const val TAG = "RolePrivilegeRepository" }

    override suspend fun getPrivilegesForRole(roleId: Long): List<Privilege> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get privileges for role: $roleId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getPrivilegesForRole", e)
        emptyList()
    }

    override suspend fun addPrivilegeToRole(roleId: Long, privilegeId: Long) {
        try {
            // no-op placeholder
        } catch (e: IOException) {
            Log.e(TAG, "Failed to add privilege to role: $roleId", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error in addPrivilegeToRole", e)
        }
    }

    override suspend fun removePrivilegeFromRole(roleId: Long, privilegeId: Long) {
        try {
            // no-op placeholder
        } catch (e: IOException) {
            Log.e(TAG, "Failed to remove privilege from role: $roleId", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error in removePrivilegeFromRole", e)
        }
    }
}
