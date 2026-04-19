package com.dexter.backend.mapper

import com.dexter.backend.dto.UserDto
import com.dexter.backend.entity.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named
import org.mapstruct.ReportingPolicy
import java.util.Date

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserMapper {

    @Mapping(source = "createDate", target = "createDate", qualifiedByName = ["dateToEpoch"])
    @Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = ["dateToEpoch"])
    fun userToUserDto(user: User): UserDto

    @Named("dateToEpoch")
    fun dateToEpoch(date: Date?): Long? = date?.time
}
