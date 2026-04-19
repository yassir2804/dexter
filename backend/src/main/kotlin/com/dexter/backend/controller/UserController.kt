package com.dexter.backend.controller

import com.dexter.backend.dto.RegisterRequestDto
import com.dexter.backend.dto.UserDto
import com.dexter.backend.service.UserService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${url.auth.register}")
class UserController(private val userService: UserService) {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun register(@RequestBody input: RegisterRequestDto): UserDto =
        userService.register(input)
}
