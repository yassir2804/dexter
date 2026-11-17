package com.dexter.backend.controller

import com.dexter.backend.dto.UserResult
import com.dexter.backend.service.AuthService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Endpoints de autenticacion que viven bajo /v1/users.
 *
 * El login (`POST /v1/users/login`) NO se maneja aqui: lo intercepta
 * `JwtAuthenticationFilter`. Este controller cubre las acciones
 * post-login: logout y obtener el usuario actual a partir del token.
 */
@RestController
@RequestMapping("/v1/users")
class AuthController(private val authService: AuthService) {

    /**
     * Logout: en un esquema JWT stateless, el servidor solo confirma
     * la operacion. El cliente es quien borra el token de su storage
     * local (DataStore, AsyncStorage, etc).
     */
    @PostMapping("/logout")
    @ResponseBody
    fun logout() {
        // No-op intencional. Endpoint existe para satisfacer el
        // contrato del cliente y dejar puerta abierta a futuras
        // estrategias (blacklist, revocacion por device, etc).
    }

    /**
     * Devuelve el usuario actualmente autenticado, leido a partir
     * del JWT que el filtro de autorizacion ya valido.
     */
    @GetMapping("/me", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun me(): UserResult =
        authService.currentUser()
}
