package com.dexter.app.data.datasource

import com.dexter.app.data.datasource.model.ProfileSummaryDto
import com.dexter.app.data.datasource.model.UserDto
import kotlinx.coroutines.flow.Flow

/**
 * Data source interface for `User` objects.
 */
interface UserDataSource {
    suspend fun getUsers(): Flow<List<UserDto>>
    suspend fun getUserById(id: Long): UserDto?
    suspend fun getUserByEmail(email: String): UserDto?
    suspend fun insertUser(userDto: UserDto)
    suspend fun updateUser(userDto: UserDto)
    suspend fun deleteUser(userDto: UserDto)
    suspend fun getProfileSummary(userId: Long): ProfileSummaryDto?
}
