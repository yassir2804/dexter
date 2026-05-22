package com.dexter.backend.security

import com.dexter.backend.mapper.UserMapper
import com.dexter.backend.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer

class AppCustomDsl(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val jwtProperties: JwtProperties,
) : AbstractHttpConfigurer<AppCustomDsl, HttpSecurity>() {

    override fun configure(http: HttpSecurity) {
        val authenticationManager = http.getSharedObject(AuthenticationManager::class.java)
        http.addFilter(JwtAuthenticationFilter(authenticationManager, userRepository, userMapper, jwtProperties))
        http.addFilter(JwtAuthorizationFilter(authenticationManager, jwtProperties))
    }
}
