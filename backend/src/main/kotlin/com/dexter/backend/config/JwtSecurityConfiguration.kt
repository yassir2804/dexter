package com.dexter.backend.config

import com.dexter.backend.mapper.UserMapper
import com.dexter.backend.repository.UserRepository
import com.dexter.backend.security.AppCustomDsl
import com.dexter.backend.security.JwtAccessDeniedHandler
import com.dexter.backend.security.JwtAuthenticationEntryPoint
import com.dexter.backend.security.JwtProperties
import com.dexter.backend.service.AppUserDetailsService
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Profile("!initlocal")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(JwtProperties::class)
class JwtSecurityConfiguration(
    private val jwtProperties: JwtProperties,
) {

    @Value("\${url.unsecure}")
    val urlUnsecure: String? = null

    @Value("\${url.auth.register}")
    val urlAuthRegister: String? = null

    @Value("\${url.health}")
    val urlHealth: String? = null

    @Value("\${url.search}")
    val urlSearch: String? = null

    @Resource
    private lateinit var userDetailsService: AppUserDetailsService

    @Resource
    private lateinit var userRepository: UserRepository

    @Resource
    private lateinit var userMapper: UserMapper

    @Resource
    private lateinit var jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint

    @Resource
    private lateinit var jwtAccessDeniedHandler: JwtAccessDeniedHandler

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager =
        authConfig.authenticationManager

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider(userDetailsService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/$urlUnsecure/**").permitAll()
                    .requestMatchers(HttpMethod.POST, urlAuthRegister).permitAll()
                    .requestMatchers(HttpMethod.GET, "/$urlHealth").permitAll()
                    .requestMatchers(HttpMethod.POST, "/v1/users/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/$urlSearch/**").permitAll()
                    .requestMatchers("/**").authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling {
                it.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                  .accessDeniedHandler(jwtAccessDeniedHandler)
            }
            .authenticationProvider(authenticationProvider())
            .with(AppCustomDsl(userRepository, userMapper, jwtProperties)) { }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration().apply {
            allowCredentials = true
            addAllowedOriginPattern("*")
            addAllowedHeader("*")
            addAllowedMethod("*")
        }
        source.registerCorsConfiguration("/**", config)
        return source
    }
}
