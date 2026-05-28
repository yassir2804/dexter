package com.dexter.backend.security

import com.dexter.backend.error.ApiError
import com.dexter.backend.error.ApiSubError
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

/**
 * Devuelve 403 con el `ApiError` estandar cuando un usuario autenticado
 * intenta acceder a un recurso para el que no tiene permisos
 * (por ejemplo, falla un `@PreAuthorize`).
 */
@Component
class JwtAccessDeniedHandler : AccessDeniedHandler {

    private val objectMapper = ObjectMapper()


    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        val apiError = ApiError(
            status = HttpStatus.FORBIDDEN,
            message = "access denied",
            debugMessage = accessDeniedException.message,
        )
        apiError.addSubError(ApiSubError("FORBIDDEN", "Insufficient privileges for this resource"))

        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = SecurityConstants.APPLICATION_JSON
        response.characterEncoding = SecurityConstants.UTF_8
        response.writer.write(objectMapper.writeValueAsString(apiError))
        response.writer.flush()
    }
}
