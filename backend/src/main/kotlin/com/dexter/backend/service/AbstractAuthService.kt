package com.dexter.backend.service

import com.dexter.backend.dto.UserResult
import com.dexter.backend.mapper.UserMapper
import com.dexter.backend.repository.UserRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AbstractAuthService(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
) : AuthService {

    override fun currentUser(): UserResult {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw AccessDeniedException("No authentication present")

        val email = auth.name
        if (email.isNullOrBlank()) {
            throw AccessDeniedException("Authentication missing principal")
        }

        return userRepository.findByEmail(email)
            .map(userMapper::userToUserResult)
            .orElseThrow { AccessDeniedException("Authenticated user not found") }
    }
}
