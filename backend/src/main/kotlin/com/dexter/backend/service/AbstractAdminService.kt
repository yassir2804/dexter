package com.dexter.backend.service

import com.dexter.backend.dto.UserResult
import com.dexter.backend.mapper.UserMapper
import com.dexter.backend.repository.RoleRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AbstractAdminService(
    @Autowired val userRepository: UserRepository,
    @Autowired val roleRepository: RoleRepository,
    @Autowired val userMapper: UserMapper,
) : AdminService {

    override fun listUsers(): List<UserResult> {
        return userRepository.findAllWithRoles().map { user ->
            userMapper.userToUserResult(user).also { result ->
                result.roles = user.roleList?.map { it.name }
            }
        }
    }

    @Transactional
    override fun toggleUserStatus(userId: Long): UserResult {
        val user = userRepository.findByIdWithRoles(userId)
            .orElseThrow { NoSuchElementException("User not found: $userId") }
        user.enabled = !user.enabled
        val saved = userRepository.save(user)
        return userMapper.userToUserResult(saved).also { result ->
            result.roles = saved.roleList?.map { it.name }
        }
    }

    @Transactional
    override fun changeUserRole(userId: Long, roleName: String): UserResult {
        val user = userRepository.findByIdWithRoles(userId)
            .orElseThrow { NoSuchElementException("User not found: $userId") }
        val role = roleRepository.findByName(roleName)
            .orElseThrow { IllegalArgumentException("Role not found: $roleName") }
        user.roleList = mutableSetOf(role)
        val saved = userRepository.save(user)
        return userMapper.userToUserResult(saved).also { result ->
            result.roles = saved.roleList?.map { it.name }
        }
    }
}
