package com.dexter.backend.repository

import com.dexter.backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(@Param("email") email: String): Optional<User>

    fun findByUsername(@Param("username") username: String): Optional<User>

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roleList")
    fun findAllWithRoles(): List<User>

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roleList WHERE u.id = :id")
    fun findByIdWithRoles(@Param("id") id: Long): Optional<User>
}
