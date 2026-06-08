package com.dexter.backend.service

import com.dexter.backend.dto.RegisterInput
import com.dexter.backend.dto.UserResult
import com.dexter.backend.entity.Availability
import com.dexter.backend.entity.Skill
import com.dexter.backend.entity.SkillLevel
import com.dexter.backend.entity.SkillRole
import com.dexter.backend.entity.User
import com.dexter.backend.mapper.UserMapper
import com.dexter.backend.repository.AvailabilityRepository
import com.dexter.backend.repository.RoleRepository
import com.dexter.backend.repository.SkillRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalTime
import java.util.Date

@Service
class AbstractUserService(
    @Autowired val userRepository: UserRepository,
    @Autowired val roleRepository: RoleRepository,
    @Autowired val skillRepository: SkillRepository,
    @Autowired val availabilityRepository: AvailabilityRepository,
    @Autowired val userMapper: UserMapper,
    @Autowired val passwordEncoder: BCryptPasswordEncoder,
) : UserService {

    override fun register(input: RegisterInput): UserResult {
        require(!input.email.isNullOrBlank()) { "email is required" }
        require(!input.password.isNullOrBlank()) { "password is required" }
        require(userRepository.findByEmail(input.email!!).isEmpty) { "email already registered" }

        val defaultRole = roleRepository.findByName("ROLE_USER").orElse(null)

        val user = User(
            email = input.email,
            password = passwordEncoder.encode(input.password),
            firstName = input.firstName,
            lastName = input.lastName,
            username = generateUsername(input.email!!, input.firstName),
            country = input.country,
            phone = input.phone,
            enabled = true,
            profileVisible = true,
            tokenExpired = false,
            rating = 0.0,
            totalSessions = 0,
            totalMatches = 0,
            createDate = Date(),
            updatedAt = Date(),
            roleList = if (defaultRole != null) setOf(defaultRole) else null,
        )

        val savedUser = userRepository.save(user)

        // ── Create availability records for selected days ────────────────────
        input.availabilityDays.forEach { dayOfWeek ->
            if (dayOfWeek in 1..7) {
                availabilityRepository.save(Availability(
                    user = savedUser,
                    dayOfWeek = dayOfWeek,
                    startTime = LocalTime.of(9, 0),      // default 9 AM
                    endTime = LocalTime.of(17, 0)        // default 5 PM
                ))
            }
        }

        input.skillsToTeach.filter { it.isNotBlank() }.forEach { name ->
            skillRepository.save(Skill(
                user = savedUser,
                name = name.trim(),
                skillRole = SkillRole.OFFERED,
                skillLevel = SkillLevel.BEGINNER,
            ))
        }
        input.skillsToLearn.filter { it.isNotBlank() }.forEach { name ->
            skillRepository.save(Skill(
                user = savedUser,
                name = name.trim(),
                skillRole = SkillRole.SOUGHT,
                skillLevel = SkillLevel.BEGINNER,
            ))
        }

        return userMapper.userToUserResult(savedUser)
    }

    /**
     * Genera un username unico a partir del prefijo del email o del firstName.
     * Si ya existe, sufija con -2, -3, etc. hasta encontrar uno libre.
     */
    private fun generateUsername(email: String, firstName: String?): String {
        val base = (firstName?.takeIf { it.isNotBlank() } ?: email.substringBefore("@"))
            .lowercase()
            .replace(Regex("[^a-z0-9]"), "")
            .ifBlank { "user" }

        var candidate = base
        var suffix = 2
        while (userRepository.findByUsername(candidate).isPresent) {
            candidate = "$base$suffix"
            suffix++
        }
        return candidate
    }
}
