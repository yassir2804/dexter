package com.dexter.backend.service

import com.dexter.backend.dto.UserResult

/**
 * Operaciones de autenticacion expuestas a los controllers.
 *
 * El login lo maneja Spring Security (JwtAuthenticationFilter),
 * por lo que aqui solo viven las acciones post-login.
 */
interface AuthService {

    /**
     * Retorna el usuario autenticado a partir del SecurityContext.
     *
     * @throws AccessDeniedException si no hay usuario autenticado.
     */
    fun currentUser(): UserResult
}
