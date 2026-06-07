package com.dexter.app.data.repository

import com.dexter.app.data.mapper.UserMapper
import com.dexter.app.data.remote.AdminRemoteDataSource
import com.dexter.app.domain.model.User
import com.dexter.app.domain.repository.AdminRepository
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val remoteDataSource: AdminRemoteDataSource,
    private val userMapper: UserMapper,
) : AdminRepository {

    override suspend fun getUsers(): Result<List<User>> = try {
        remoteDataSource.getUsers().map { dtos ->
            dtos.map { userMapper.mapToDomain(it) }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun toggleUserStatus(userId: Long): Result<User> = try {
        remoteDataSource.toggleUserStatus(userId).map { userMapper.mapToDomain(it) }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun changeUserRole(userId: Long, roleName: String): Result<User> = try {
        remoteDataSource.changeUserRole(userId, roleName).map { userMapper.mapToDomain(it) }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
