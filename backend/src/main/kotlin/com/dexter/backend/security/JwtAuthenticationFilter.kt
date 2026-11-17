package com.dexter.backend.security

import com.dexter.backend.dto.UserLoginInput
import com.dexter.backend.error.ApiError
import com.dexter.backend.error.ApiSubError
import com.dexter.backend.mapper.UserMapper
import com.dexter.backend.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.security.Key
import java.util.Date

class JwtAuthenticationFilter(
    private val authManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val jwtProperties: JwtProperties,
) : UsernamePasswordAuthenticationFilter() {

    init {
        setFilterProcessesUrl("/v1/users/login")
        setAuthenticationFailureHandler { _, response, exception ->
            val isDisabled = exception is org.springframework.security.authentication.DisabledException
            val apiError = ApiError(
                status = HttpStatus.UNAUTHORIZED,
                message = "authentication failed",
                debugMessage = exception.message,
            )
            if (isDisabled) {
                apiError.addSubError(ApiSubError("USER_DISABLED", "User account is disabled"))
            } else {
                apiError.addSubError(ApiSubError("INVALID_CREDENTIALS", "Invalid username or password"))
            }

            response.status = jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = SecurityConstants.APPLICATION_JSON
            response.characterEncoding = SecurityConstants.UTF_8
            response.writer.print(ObjectMapper().writeValueAsString(apiError))
            response.writer.flush()
        }
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): Authentication {
        if (request.method != "POST") {
            throw AuthenticationServiceException("Authentication method not supported: ${request.method}")
        }

        return try {
            val userLoginInput: UserLoginInput = ObjectMapper()
                .readValue(request.inputStream, UserLoginInput::class.java)
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    userLoginInput.username,
                    userLoginInput.password,
                    ArrayList(),
                )
            )
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
        authentication: Authentication,
    ) {
        val objectMapper = ObjectMapper()
        val username = (authentication.principal as org.springframework.security.core.userdetails.User).username
        val authorities = authentication.authorities.map { it.authority }

        val token = Jwts.builder()
            .signWith(signingKey(jwtProperties.secret), SignatureAlgorithm.HS512)
            .setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
            .setIssuer(jwtProperties.issuer)
            .setAudience(jwtProperties.audience)
            .setSubject(username)
            .claim("authorities", authorities)
            .setExpiration(Date(System.currentTimeMillis() + jwtProperties.expiration))
            .compact()

        val userDto = userRepository.findByEmail(username)
            .map(userMapper::userToUserResult)
            .orElse(null)
        userDto?.roles = authorities.filterNotNull().filter { it.startsWith("ROLE_") }

        response.addHeader(HttpHeaders.AUTHORIZATION, SecurityConstants.TOKEN_PREFIX + token)
        response.contentType = SecurityConstants.APPLICATION_JSON
        response.characterEncoding = SecurityConstants.UTF_8
        response.writer.print(objectMapper.writeValueAsString(userDto))
        response.writer.flush()
    }
}

internal fun signingKey(secret: String): Key =
    Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))
