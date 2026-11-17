package com.dexter.backend.service

import com.dexter.backend.dto.UserResult

interface AdminService {
    fun listUsers(): List<UserResult>
    fun toggleUserStatus(userId: Long): UserResult
    fun changeUserRole(userId: Long, roleName: String): UserResult
}
