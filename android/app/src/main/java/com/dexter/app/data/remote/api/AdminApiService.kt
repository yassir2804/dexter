package com.dexter.app.data.remote.api

import com.dexter.app.data.datasource.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

data class ChangeRoleRequestDto(val roleName: String)

interface AdminApiService {

    @GET("admin/users")
    suspend fun getUsers(): Response<List<UserDto>>

    @PUT("admin/users/{id}/toggle-status")
    suspend fun toggleUserStatus(@Path("id") id: Long): Response<UserDto>

    @PUT("admin/users/{id}/role")
    suspend fun changeUserRole(
        @Path("id") id: Long,
        @Body body: ChangeRoleRequestDto
    ): Response<UserDto>
}
