package com.dexter.backend.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

/**
 * Perfil `initlocal`: deja todo abierto para cargar datos iniciales sin
 * pelear contra la seguridad. NUNCA usar en producción.
 */
@Profile("initlocal")
@Configuration
@EnableWebSecurity
class OpenSecurityConfiguration {

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.disable() }
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
        return http.build()
    }
}
