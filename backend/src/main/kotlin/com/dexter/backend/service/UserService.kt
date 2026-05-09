package com.dexter.backend.service

import com.dexter.backend.dto.RegisterInput
import com.dexter.backend.dto.UserResult

interface UserService {

    /**
     * Registra un usuario nuevo.
     *
     * @param input datos desde el formulario de registro del front
     * @return el usuario creado, mapeado a UserDto
     */
    fun register(input: RegisterInput): UserResult
}
