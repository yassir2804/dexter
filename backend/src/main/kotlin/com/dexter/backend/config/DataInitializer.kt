package com.dexter.backend.config

import com.dexter.backend.entity.Privilege
import com.dexter.backend.entity.Role
import com.dexter.backend.entity.User
import com.dexter.backend.repository.PrivilegeRepository
import com.dexter.backend.repository.RoleRepository
import com.dexter.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import jakarta.transaction.Transactional
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.util.Date

@Component
class DataInitializer(
    private val privilegeRepository: PrivilegeRepository,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    @Transactional
    override fun run(args: ApplicationArguments) {
        val readPrivilege = privilegeRepository.findByName("READ_PRIVILEGE").orElseGet {
            log.info("Creating READ_PRIVILEGE")
            privilegeRepository.save(Privilege(name = "READ_PRIVILEGE"))
        }
        val writePrivilege = privilegeRepository.findByName("WRITE_PRIVILEGE").orElseGet {
            log.info("Creating WRITE_PRIVILEGE")
            privilegeRepository.save(Privilege(name = "WRITE_PRIVILEGE"))
        }

        val roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseGet {
            log.info("Creating ROLE_ADMIN")
            roleRepository.save(Role(name = "ROLE_ADMIN", privilegeList = setOf(readPrivilege, writePrivilege)))
        }
        val roleUser = roleRepository.findByName("ROLE_USER").orElseGet {
            log.info("Creating ROLE_USER")
            roleRepository.save(Role(name = "ROLE_USER", privilegeList = setOf(readPrivilege)))
        }

        if (userRepository.findByEmail("admin@dexter.app").isEmpty) {
            log.info("Creating admin user: admin@dexter.app")
            userRepository.save(User(
                email = "admin@dexter.app",
                password = passwordEncoder.encode("admin123"),
                firstName = "Admin",
                lastName = "Dexter",
                username = "admin",
                bio = "Administrador del sistema",
                country = "CR",
                enabled = true,
                profileVisible = false,
                tokenExpired = false,
                rating = 0.0,
                totalSessions = 0,
                totalMatches = 0,
                createDate = Date(),
                updatedAt = Date(),
                roleList = setOf(roleAdmin),
            ))
        } else {
            log.info("Admin user already exists — ensuring ROLE_ADMIN is assigned")
            val admin = userRepository.findByEmail("admin@dexter.app").get()
            val hasAdminRole = admin.roleList?.any { it.name == "ROLE_ADMIN" } == true
            if (!hasAdminRole) {
                admin.roleList = setOf(roleAdmin)
                userRepository.save(admin)
                log.info("Assigned ROLE_ADMIN to existing admin user")
            }
        }
    }
}
