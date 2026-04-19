package com.dexter.backend.mapper

import com.dexter.backend.dto.RoleDetails
import com.dexter.backend.entity.Role
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface RoleMapper {
    fun roleToRoleDetails(role: Role): RoleDetails

    fun roleListToRoleDetailsList(roleList: Set<Role>?): List<RoleDetails>
}
