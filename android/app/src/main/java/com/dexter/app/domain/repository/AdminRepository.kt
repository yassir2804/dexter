package com.dexter.app.domain.repository

import com.dexter.app.domain.model.User

interface AdminRepository {
    suspend fun getUsers(): Result<List<User>>
    suspend fun toggleUserStatus(userId: Long): Result<User>
    suspend fun changeUserRole(userId: Long, roleName: String): Result<User>
}
