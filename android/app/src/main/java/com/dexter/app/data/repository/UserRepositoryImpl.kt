package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.datasource.UserDataSource
import com.dexter.app.data.mapper.UserMapper
import com.dexter.app.domain.model.User
import com.dexter.app.domain.repository.UserRepository
import java.io.IOException

/**
 * Placeholder implementation of [UserRepository]:
 * uses a mapper dependency, logs and handles IO/unknown errors, and returns
 * safe defaults. Replace the internals with real data-source calls when
 * integrating a backend.
 */
class UserRepositoryImpl(
    private val dataSource: UserDataSource,
    private val userMapper: UserMapper
) : UserRepository {

    companion object { private const val TAG = "UserRepository" }

    override suspend fun getUserById(id: Long): User? = try {
        // TODO: replace with dataSource call + mapping
        null
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get user by id: $id", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getUserById", e)
        null
    }

    override suspend fun getAllUsers(): List<User> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get all users", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getAllUsers", e)
        emptyList()
    }

    override suspend fun findUsersByName(query: String): List<User> = try {
        emptyList()
    } catch (e: IOException) {
        Log.e(TAG, "Failed to search users: $query", e)
        emptyList()
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in findUsersByName", e)
        emptyList()
    }

    override suspend fun saveUser(user: User): User = try {
        // echo back for now
        user
    } catch (e: IOException) {
        Log.e(TAG, "Failed to save user", e)
        user
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in saveUser", e)
        user
    }

    override suspend fun deleteUser(id: Long) { try {
        // no-op
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in deleteUser", e)
    } }

    override suspend fun login(email: String, password: String): User? = try {
        val userDto = dataSource.getUserByEmail(email)
        val user = userDto?.let { userMapper.mapToDomain(it) }
        if (user != null && user.password == password) user else null
    } catch (e: IOException) {
        Log.e(TAG, "Network error during login", e)
        null
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in login", e)
        null
    }
}
