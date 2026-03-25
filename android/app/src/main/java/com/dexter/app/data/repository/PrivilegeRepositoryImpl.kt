package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.domain.model.Privilege
import com.dexter.app.domain.repository.PrivilegeRepository
import java.io.IOException

class PrivilegeRepositoryImpl : PrivilegeRepository {

    companion object { private const val TAG = "PrivilegeRepository" }

    override suspend fun getPrivilegeById(id: Long): Privilege? = try {
        null
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get privilege by id: $id", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getPrivilegeById", e)
        null
    }

    override suspend fun getAllPrivileges(): List<Privilege> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get all privileges", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getAllPrivileges", e)
        emptyList()
    }

    override suspend fun savePrivilege(privilege: Privilege): Privilege = try {
        privilege
    } catch (e: IOException) {
        Log.e(TAG, "Failed to save privilege", e)
        privilege
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in savePrivilege", e)
        privilege
    }

    override suspend fun deletePrivilege(id: Long) {
        try {
            // no-op
        } catch (e: Exception) {
            Log.e(TAG, "Unknown error in deletePrivilege", e)
        }
    }
}
