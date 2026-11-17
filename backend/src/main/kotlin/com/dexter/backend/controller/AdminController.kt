package com.dexter.backend.controller

import com.dexter.backend.dto.ChangeRoleRequest
import com.dexter.backend.dto.UserResult
import com.dexter.backend.service.AdminService
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(private val adminService: AdminService) {

    @GetMapping("/users", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listUsers(): List<UserResult> = adminService.listUsers()

    @PutMapping("/users/{id}/toggle-status", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun toggleUserStatus(@PathVariable id: Long): UserResult =
        adminService.toggleUserStatus(id)

    @PutMapping("/users/{id}/role", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun changeUserRole(
        @PathVariable id: Long,
        @RequestBody request: ChangeRoleRequest
    ): UserResult = adminService.changeUserRole(id, request.roleName)
}
