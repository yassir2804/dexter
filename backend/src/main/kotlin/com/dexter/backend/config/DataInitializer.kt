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
        val readPrivilege = privilegeRepository.findAllByName("READ_PRIVILEGE").firstOrNull()
            ?: privilegeRepository.save(Privilege(name = "READ_PRIVILEGE")).also {
                log.info("Created READ_PRIVILEGE")
            }
        val writePrivilege = privilegeRepository.findAllByName("WRITE_PRIVILEGE").firstOrNull()
            ?: privilegeRepository.save(Privilege(name = "WRITE_PRIVILEGE")).also {
                log.info("Created WRITE_PRIVILEGE")
            }

        val roleAdmin = roleRepository.findAllByName("ROLE_ADMIN").firstOrNull()
            ?: roleRepository.save(Role(name = "ROLE_ADMIN", privilegeList = setOf(readPrivilege, writePrivilege))).also {
                log.info("Created ROLE_ADMIN")
            }
        roleRepository.findAllByName("ROLE_USER").firstOrNull()
            ?: roleRepository.save(Role(name = "ROLE_USER", privilegeList = setOf(readPrivilege))).also {
                log.info("Created ROLE_USER")
            }

        val adminCandidates = userRepository.findAllByEmail("admin@dexter.app")
        if (adminCandidates.isEmpty()) {
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
                phone = "+506 8888-0000",
                roleList = setOf(roleAdmin),
            ))
        } else {
            log.info("Admin user already exists — ensuring ROLE_ADMIN is assigned")
            val admin = adminCandidates.first()
            val hasAdminRole = admin.roleList?.any { it.name == "ROLE_ADMIN" } == true
            if (!hasAdminRole) {
                admin.roleList = HashSet(admin.roleList ?: emptySet()) + roleAdmin
                userRepository.save(admin)
                log.info("Assigned ROLE_ADMIN to existing admin user")
            }
        }
    }
}
