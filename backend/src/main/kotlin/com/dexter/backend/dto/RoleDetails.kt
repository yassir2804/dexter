package com.dexter.backend.dto

data class RoleDetails(
    var id: Long? = null,
    var name: String? = null,
    var privileges: List<PrivilegeDetails>? = null,
)
