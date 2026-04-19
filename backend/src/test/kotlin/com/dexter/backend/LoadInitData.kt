package com.dexter.backend

import com.dexter.backend.entity.Privilege
import com.dexter.backend.entity.Role
import com.dexter.backend.entity.User
import com.dexter.backend.repository.PrivilegeRepository
import com.dexter.backend.repository.RoleRepository
import com.dexter.backend.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import java.util.Date

/**
 * Ejecutar con el perfil `initlocal` (ddl-auto=create) para resetear DB y
 * sembrar roles + privilegios + usuario admin:
 *
 *     ./gradlew test --tests com.dexter.backend.LoadInitData -Dspring.profiles.active=initlocal
 */
@SpringBootTest
@ActiveProfiles("initlocal")
class LoadInitData {

    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var roleRepository: RoleRepository
    @Autowired lateinit var privilegeRepository: PrivilegeRepository

    private val passwordEncoder = BCryptPasswordEncoder()

    @Test
    fun seed() {
        val readPrivilege = privilegeRepository.save(Privilege(name = "READ_PRIVILEGE"))
        val writePrivilege = privilegeRepository.save(Privilege(name = "WRITE_PRIVILEGE"))

        val adminRole = roleRepository.save(
            Role(
                name = "ROLE_ADMIN",
                privilegeList = setOf(readPrivilege, writePrivilege),
            )
        )
        val userRole = roleRepository.save(
            Role(
                name = "ROLE_USER",
                privilegeList = setOf(readPrivilege),
            )
        )

        userRepository.save(
            User(
                email = "admin@dexter.app",
                password = passwordEncoder.encode("admin123"),
                firstName = "Admin",
                lastName = "Dexter",
                username = "admin",
                country = "CR",
                enabled = true,
                tokenExpired = false,
                rating = 0.0,
                totalSessions = 0,
                totalMatches = 0,
                createDate = Date(),
                updatedAt = Date(),
                roleList = setOf(adminRole),
            )
        )

        userRepository.save(
            User(
                email = "user@dexter.app",
                password = passwordEncoder.encode("user123"),
                firstName = "Regular",
                lastName = "User",
                username = "user",
                country = "CR",
                enabled = true,
                tokenExpired = false,
                rating = 0.0,
                totalSessions = 0,
                totalMatches = 0,
                createDate = Date(),
                updatedAt = Date(),
                roleList = setOf(userRole),
            )
        )

        println("Seeded: ${privilegeRepository.count()} privileges, ${roleRepository.count()} roles, ${userRepository.count()} users")
    }
}
