package com.dexter.backend.service

import com.dexter.backend.entity.Role
import com.dexter.backend.entity.User
import com.dexter.backend.repository.RoleRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AppUserDetailsService(
    @Autowired val userRepository: UserRepository,
    @Autowired val roleRepository: RoleRepository,
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByEmail(username).orElse(null)
            ?: return org.springframework.security.core.userdetails.User(
                "", "", true, true, true, true,
                getAuthorities(
                    listOfNotNull(
                        roleRepository.findByName("ROLE_USER").orElse(null)
                    )
                )
            )

        return org.springframework.security.core.userdetails.User(
            user.email ?: "",
            user.password ?: "",
            user.enabled,
            true,
            true,
            true,
            getAuthorities(user.roleList?.toList() ?: emptyList())
        )
    }

    private fun getAuthorities(roles: Collection<Role>): Collection<GrantedAuthority> =
        roles.flatMap { role ->
            sequenceOf(SimpleGrantedAuthority(role.name)) +
                    role.privilegeList.map { SimpleGrantedAuthority(it.name) }
        }
}
