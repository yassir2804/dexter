package com.dexter.backend.repository

import com.dexter.backend.entity.Privilege
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PrivilegeRepository : JpaRepository<Privilege, Long> {
    fun findByName(@Param("name") name: String): Optional<Privilege>
    fun findAllByName(name: String): List<Privilege>
}
