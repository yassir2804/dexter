package com.dexter.app.data.datasource

import android.util.Log
import com.dexter.app.data.datasource.model.ProfileSummaryDto
import com.dexter.app.data.datasource.model.RegisterRequestDto
import com.dexter.app.data.datasource.model.UserDto
import com.dexter.app.data.mapper.ProfileSummaryMapper
import com.dexter.app.data.mapper.UserMapper
import com.dexter.app.data.remote.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Implementation of [UserDataSource].
 *
 * For [getProfileSummary]: tries the API first via [UserRemoteDataSource],
 * falls back to [UserProvider] if API is unavailable.
 * Logs a warning when using fallback so it's visible in Logcat.
 *
 * Same pattern as [SearchDataSourceImpl].
 */
class UserDataSourceImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val userMapper: UserMapper,
    private val profileSummaryMapper: ProfileSummaryMapper
) : UserDataSource {

    companion object {
        private const val TAG = "UserDataSource"
    }

    override suspend fun getProfileSummary(userId: Long): ProfileSummaryDto? {
        val result = remoteDataSource.getProfileSummary(userId)
        return result.getOrNull()
    }

    /**
     * Sends registration data to the API via POST /auth/register.
     * Returns the created UserDto if successful, null if API fails.
     */
    override suspend fun insertUser(request: RegisterRequestDto): UserDto? {
        val result = remoteDataSource.register(request)
        // Propaga la excepción real (mensaje del backend traducido por ApiErrorParser)
        // en vez de tragarla con getOrNull() → null, que ocultaba la causa.
        return result.getOrThrow()
    }

    // ── Below methods still use UserProvider (no API endpoints yet) ──

    override suspend fun getUsers(): Flow<List<UserDto>> = flow {
        val users = UserProvider.findAllUsers()
        emit(users.map { userMapper.mapToDto(it) })
    }

    override suspend fun getUserById(id: Long): UserDto? =
        UserProvider.findUserById(id)?.let { userMapper.mapToDto(it) }

    override suspend fun getUserByEmail(email: String): UserDto? =
        UserProvider.findUserByEmail(email)?.let { userMapper.mapToDto(it) }

    override suspend fun updateProfile(profile: ProfileSummaryDto): ProfileSummaryDto? {
        val result = remoteDataSource.updateProfile(profile)
        return result.getOrNull()
            ?: run {
                Log.w(TAG, "API failed for updateProfile, changes not saved")
                null
            }
    }

    override suspend fun deleteUser(userDto: UserDto) {
        // Mock implementation
    }
}
