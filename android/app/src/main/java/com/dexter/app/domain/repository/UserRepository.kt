package com.dexter.app.domain.repository

import com.dexter.app.domain.model.User

/**
 * Repository interface for user-related operations.
 *
 * Defined in the domain layer as a contract. Implementations live in the data layer
 * (e.g. network or local mock). ViewModels depend on this interface, not on concrete
 * implementations (Dependency Inversion).
 */
interface UserRepository {
    suspend fun getUserById(id: Long): User?
    suspend fun getAllUsers(): List<User>
    suspend fun findUsersByName(query: String): List<User>
    suspend fun saveUser(user: User): User
    suspend fun deleteUser(id: Long)
}
