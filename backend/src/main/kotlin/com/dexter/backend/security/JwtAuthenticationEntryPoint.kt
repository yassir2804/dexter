package com.dexter.backend.security

import com.dexter.backend.error.ApiError
import com.dexter.backend.error.ApiSubError
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

/**
 * Devuelve 401 con el `ApiError` estandar cuando una request anonima
 * o con token invalido intenta acceder a un endpoint protegido.
 *
 * Reemplaza el 403 plano que Spring Security devuelve por defecto.
 */
@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {

    private val objectMapper = ObjectMapper()


    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        val apiError = ApiError(
            status = HttpStatus.UNAUTHORIZED,
            message = "authentication required",
            debugMessage = authException.message,
        )
        apiError.addSubError(ApiSubError("UNAUTHORIZED", "Missing or invalid authentication token"))

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = SecurityConstants.APPLICATION_JSON
        response.characterEncoding = SecurityConstants.UTF_8
        response.writer.write(objectMapper.writeValueAsString(apiError))
        response.writer.flush()
    }
}
