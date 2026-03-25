package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.domain.model.Role
import com.dexter.app.domain.repository.RoleRepository
import java.io.IOException

class RoleRepositoryImpl : RoleRepository {

    companion object { private const val TAG = "RoleRepository" }

    override suspend fun getRoleById(id: Long): Role? = try {
        null
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get role by id: $id", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getRoleById", e)
        null
    }

    override suspend fun getAllRoles(): List<Role> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get all roles", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getAllRoles", e)
        emptyList()
    }

    override suspend fun saveRole(role: Role): Role = try {
        role
    } catch (e: IOException) {
        Log.e(TAG, "Failed to save role", e)
        role
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in saveRole", e)
        role
    }

    override suspend fun deleteRole(id: Long) {
        try {
            // no-op
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error in deleteRole", e)
        }
    }
}
