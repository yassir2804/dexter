package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.domain.model.Role
import com.dexter.app.domain.repository.UserRoleRepository
import java.io.IOException

class UserRoleRepositoryImpl : UserRoleRepository {

    companion object { private const val TAG = "UserRoleRepository" }

    override suspend fun getRolesForUser(userId: Long): List<Role> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get roles for user: $userId", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getRolesForUser", e)
        emptyList()
    }

    override suspend fun addRoleToUser(userId: Long, roleId: Long) {
        try {
            // no-op
        } catch (e: IOException) {
            Log.e(TAG, "Failed to add role to user: $userId", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error in addRoleToUser", e)
        }
    }

    override suspend fun removeRoleFromUser(userId: Long, roleId: Long) {
        try {
            // no-op
        } catch (e: IOException) {
            Log.e(TAG, "Failed to remove role from user: $userId", e)
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error in removeRoleFromUser", e)
        }
    }
}
