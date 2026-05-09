package com.dexter.backend.service

import com.dexter.backend.dto.RegisterRequestDto
import com.dexter.backend.dto.UserDto
import com.dexter.backend.entity.Skill
import com.dexter.backend.entity.SkillLevel
import com.dexter.backend.entity.SkillRole
import com.dexter.backend.entity.User
import com.dexter.backend.mapper.UserMapper
import com.dexter.backend.repository.RoleRepository
import com.dexter.backend.repository.SkillRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AbstractUserService(
    @Autowired val userRepository: UserRepository,
    @Autowired val roleRepository: RoleRepository,
    @Autowired val skillRepository: SkillRepository,
    @Autowired val userMapper: UserMapper,
    @Autowired val passwordEncoder: BCryptPasswordEncoder,
) : UserService {

    override fun register(input: RegisterRequestDto): UserDto {
        require(!input.email.isNullOrBlank()) { "email is required" }
        require(!input.password.isNullOrBlank()) { "password is required" }
        require(userRepository.findByEmail(input.email!!).isEmpty) { "email already registered" }

        val defaultRole = roleRepository.findByName("ROLE_USER").orElse(null)

        val user = User(
            email = input.email,
            password = passwordEncoder.encode(input.password),
            firstName = input.firstName,
            lastName = input.lastName,
            country = input.country,
            enabled = true,
            tokenExpired = false,
            rating = 0.0,
            totalSessions = 0,
            totalMatches = 0,
            createDate = Date(),
            updatedAt = Date(),
            roleList = if (defaultRole != null) setOf(defaultRole) else null,
        )

        val savedUser = userRepository.save(user)

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

        return userMapper.userToUserDto(savedUser)
    }
}
