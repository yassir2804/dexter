package com.dexter.backend

import com.dexter.backend.entity.Match
import com.dexter.backend.entity.MatchStatus
import com.dexter.backend.entity.Privilege
import com.dexter.backend.entity.Review
import com.dexter.backend.entity.Role
import com.dexter.backend.entity.Session
import com.dexter.backend.entity.SessionStatus
import com.dexter.backend.entity.Skill
import com.dexter.backend.entity.SkillLevel
import com.dexter.backend.entity.SkillRole
import com.dexter.backend.entity.User
import com.dexter.backend.repository.MatchRepository
import com.dexter.backend.repository.PrivilegeRepository
import com.dexter.backend.repository.ReviewRepository
import com.dexter.backend.repository.RoleRepository
import com.dexter.backend.repository.SessionRepository
import com.dexter.backend.repository.SkillRepository
import com.dexter.backend.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import java.util.Date

/**
 * Ejecutar con el perfil `initlocal` (ddl-auto=create) para resetear DB y
 * sembrar roles, privilegios, usuarios, skills, matches, sessions y reviews:
 *
 *     ./gradlew test --tests com.dexter.backend.SeedInitData -Dspring.profiles.active=initlocal
 */
@SpringBootTest
@ActiveProfiles("initlocal")
class SeedInitData {

    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var roleRepository: RoleRepository
    @Autowired lateinit var privilegeRepository: PrivilegeRepository
    @Autowired lateinit var skillRepository: SkillRepository
    @Autowired lateinit var matchRepository: MatchRepository
    @Autowired lateinit var sessionRepository: SessionRepository
    @Autowired lateinit var reviewRepository: ReviewRepository

    private val passwordEncoder = BCryptPasswordEncoder()

    @Test
    fun seed() {
        // ── Roles + Privilegios ─────────────────────────────
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

        // ── Users ───────────────────────────────────────────
        val adminUser = userRepository.save(
            User(
                email = "admin@dexter.app",
                password = passwordEncoder.encode("admin123"),
                firstName = "Admin",
                lastName = "Dexter",
                username = "admin",
                bio = "Músico profesional con 10 años de experiencia",
                country = "CR",
                enabled = true,
                tokenExpired = false,
                rating = 4.8,
                totalSessions = 12,
                totalMatches = 5,
                createDate = Date(),
                updatedAt = Date(),
                roleList = setOf(adminRole),
            )
        )

        val regularUser = userRepository.save(
            User(
                email = "user@dexter.app",
                password = passwordEncoder.encode("user123"),
                firstName = "Regular",
                lastName = "User",
                username = "user",
                bio = "Profesora de inglés y fotógrafa aficionada",
                country = "CR",
                enabled = true,
                tokenExpired = false,
                rating = 4.5,
                totalSessions = 8,
                totalMatches = 3,
                createDate = Date(),
                updatedAt = Date(),
                roleList = setOf(userRole),
            )
        )

        // ── Skills ──────────────────────────────────────────
        // Cada user tiene varias skills mezclando OFFERED y SOUGHT
        // en distintas categorías para que Search tenga variedad.
        skillRepository.saveAll(listOf(
            // Admin
            Skill(user = adminUser, name = "Guitarra", category = "Música",
                  skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = adminUser, name = "Piano", category = "Música",
                  skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = adminUser, name = "Inglés", category = "Idiomas",
                  skillRole = SkillRole.SOUGHT, skillLevel = SkillLevel.BEGINNER),
            Skill(user = adminUser, name = "Francés", category = "Idiomas",
                  skillRole = SkillRole.SOUGHT, skillLevel = SkillLevel.BEGINNER),
            // Regular
            Skill(user = regularUser, name = "Inglés", category = "Idiomas",
                  skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = regularUser, name = "Fotografía", category = "Arte",
                  skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = regularUser, name = "Guitarra", category = "Música",
                  skillRole = SkillRole.SOUGHT, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = regularUser, name = "Kotlin", category = "Programación",
                  skillRole = SkillRole.SOUGHT, skillLevel = SkillLevel.BEGINNER),
        ))

        // ── Matches ─────────────────────────────────────────
        val match1 = matchRepository.save(Match(
            initiator = adminUser,
            receiver = regularUser,
            compatibilityScore = 0.92,
            status = MatchStatus.ACCEPTED,
            createdAt = Date(),
        ))
        matchRepository.save(Match(
            initiator = regularUser,
            receiver = adminUser,
            compatibilityScore = 0.78,
            status = MatchStatus.PENDING,
            createdAt = Date(),
        ))

        // ── Session completada (para poder sembrar review) ──
        val guitarraSkill = skillRepository.findByUserId(adminUser.id!!)
            .first { it.skillRole == SkillRole.OFFERED && it.name == "Guitarra" }

        val session1 = sessionRepository.save(Session(
            match = match1,
            skill = guitarraSkill,
            sessionNumber = 1,
            title = "Introducción a la guitarra",
            description = "Primera clase — acordes básicos",
            status = SessionStatus.COMPLETED,
            scheduledAt = Date(),
            completedAt = Date(),
            createdAt = Date(),
        ))

        // ── Review ──────────────────────────────────────────
        reviewRepository.save(Review(
            reviewer = regularUser,
            reviewee = adminUser,
            session = session1,
            rating = 5,
            comment = "Excelente profesor, muy paciente.",
            createdAt = Date(),
        ))

        println("Seeded: ${privilegeRepository.count()} privileges, ${roleRepository.count()} roles, " +
                "${userRepository.count()} users, ${skillRepository.count()} skills, " +
                "${matchRepository.count()} matches, ${sessionRepository.count()} sessions, " +
                "${reviewRepository.count()} reviews")
    }
}
