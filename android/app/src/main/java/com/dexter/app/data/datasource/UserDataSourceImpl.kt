package com.dexter.app.data.datasource

import com.dexter.app.data.datasource.model.ProfileSummaryDto
import com.dexter.app.data.datasource.model.UserDto
import com.dexter.app.data.mapper.ProfileSummaryMapper
import com.dexter.app.data.mapper.UserMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * UserDataSource implementation backed by an in-memory provider.
 */
class UserDataSourceImpl(
    private val userMapper: UserMapper,
    private val profileSummaryMapper: ProfileSummaryMapper
) : UserDataSource {

    override suspend fun getUsers(): Flow<List<UserDto>> = flow {
        val users = UserProvider.findAllUsers()
        emit(users.map { userMapper.mapToDto(it) })
    }

    override suspend fun getUserById(id: Long): UserDto? =
        UserProvider.findUserById(id)?.let { userMapper.mapToDto(it) }

    override suspend fun getUserByEmail(email: String): UserDto? =
        UserProvider.findUserByEmail(email)?.let { userMapper.mapToDto(it) }

    override suspend fun insertUser(userDto: UserDto) {
        // Mock implementation: no persistence in provider
    }

    override suspend fun updateUser(userDto: UserDto) {
        // Mock implementation
    }

    override suspend fun deleteUser(userDto: UserDto) {
        // Mock implementation
    }

    override suspend fun getProfileSummary(userId: Long): ProfileSummaryDto? =
        UserProvider.findProfileSummary(userId)?.let { profileSummaryMapper.mapToDto(it) }
}
