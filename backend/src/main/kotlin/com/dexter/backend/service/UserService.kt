package com.dexter.backend.service

import com.dexter.backend.dto.RegisterRequestDto
import com.dexter.backend.dto.UserDto

interface UserService {

    /**
     * Registra un usuario nuevo.
     *
     * @param input datos desde el formulario de registro del front
     * @return el usuario creado, mapeado a UserDto
     */
    fun register(input: RegisterRequestDto): UserDto
}
