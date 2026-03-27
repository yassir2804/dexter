package com.dexter.app.data.repository

import android.util.Log
import com.dexter.app.data.datasource.UserDataSource
import com.dexter.app.data.mapper.ProfileSummaryMapper
import com.dexter.app.data.mapper.UserMapper
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.ProfileSummary
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
    private val userMapper: UserMapper,
    private val profileSummaryMapper: ProfileSummaryMapper
) : UserRepository {

    companion object { private const val TAG = "UserRepository" }

    override suspend fun getUserById(id: Long): Result<User> = try {
        val userDto = dataSource.getUserById(id)
        if (userDto != null) {
            Result.success(userMapper.mapToDomain(userDto))
        } else {
            Result.failure(DomainError.UserError("User not found"))
        }
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get user by id: $id", e)
        Result.failure(DomainError.NetworkError("Network error fetching user $id", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getUserById", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching user $id", e))
    }

    override suspend fun getAllUsers(): Result<List<User>> = try {
        // TODO: replace with dataSource call + mapping
        Result.success(emptyList())
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get all users", e)
        Result.failure(DomainError.NetworkError("Network error fetching users", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getAllUsers", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching users", e))
    }

    override suspend fun findUsersByName(query: String): Result<List<User>> = try {
        // TODO: replace with dataSource call + mapping
        Result.success(emptyList())
    } catch (e: IOException) {
        Log.e(TAG, "Failed to search users: $query", e)
        Result.failure(DomainError.NetworkError("Network error searching users", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in findUsersByName", e)
        Result.failure(DomainError.UnknownError("Unknown error searching users", e))
    }

    override suspend fun saveUser(user: User): Result<User> = try {
        // TODO: replace with dataSource call + mapping
        Result.success(user)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to save user", e)
        Result.failure(DomainError.NetworkError("Network error saving user", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in saveUser", e)
        Result.failure(DomainError.UnknownError("Unknown error saving user", e))
    }

    override suspend fun deleteUser(id: Long): Result<Unit> = try {
        // TODO: replace with dataSource call
        Result.success(Unit)
    } catch (e: IOException) {
        Log.e(TAG, "Failed to delete user: $id", e)
        Result.failure(DomainError.NetworkError("Network error deleting user $id", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in deleteUser", e)
        Result.failure(DomainError.UnknownError("Unknown error deleting user $id", e))
    }

    override suspend fun getProfileSummary(userId: Long): Result<ProfileSummary> = try {
        val dto = dataSource.getProfileSummary(userId)
        if (dto != null) {
            Result.success(profileSummaryMapper.mapToDomain(dto))
        } else {
            Result.failure(DomainError.UserError("Profile not found"))
        }
    } catch (e: IOException) {
        Log.e(TAG, "Failed to get profile summary for user: $userId", e)
        Result.failure(DomainError.NetworkError("Network error fetching profile", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in getProfileSummary", e)
        Result.failure(DomainError.UnknownError("Unknown error fetching profile", e))
    }

    override suspend fun login(email: String, password: String): Result<User> = try {
        val userDto = dataSource.getUserByEmail(email)
        val user = userDto?.let { userMapper.mapToDomain(it) }
        if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(DomainError.AuthError("Credenciales Inválidas"))
        }
    } catch (e: IOException) {
        Log.e(TAG, "Network error during login", e)
        Result.failure(DomainError.NetworkError("Network error during login", e))
    } catch (e: Exception) {
        Log.e(TAG, "Unknown error in login", e)
        Result.failure(DomainError.UnknownError("Unknown error during login", e))
    }

}