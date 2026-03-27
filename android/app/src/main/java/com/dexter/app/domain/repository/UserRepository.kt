package com.dexter.app.domain.repository

import com.dexter.app.domain.model.ProfileSummary
import com.dexter.app.domain.model.User

/**
 * Repository interface for user-related operations.
 *
 * Defined in the domain layer as a contract. Implementations live in the data layer
 * (e.g. network or local mock). ViewModels depend on this interface, not on concrete
 * implementations (Dependency Inversion).
 */
interface UserRepository {
    suspend fun getUserById(id: Long): Result<User>
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun findUsersByName(query: String): Result<List<User>>
    suspend fun saveUser(user: User): Result<User>
    suspend fun deleteUser(id: Long): Result<Unit>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getProfileSummary(userId: Long): Result<ProfileSummary>
}