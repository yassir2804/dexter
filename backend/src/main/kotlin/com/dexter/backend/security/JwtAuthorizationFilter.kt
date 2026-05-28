package com.dexter.backend.security

import io.jsonwebtoken.Jwts
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.io.IOException

class JwtAuthorizationFilter(
    authenticationManager: AuthenticationManager,
    private val jwtProperties: JwtProperties,
) : BasicAuthenticationFilter(authenticationManager) {

    @Throws(IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        var authorizationToken = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (authorizationToken != null && authorizationToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            authorizationToken = authorizationToken.replaceFirst(SecurityConstants.TOKEN_PREFIX.toRegex(), "")
            val claims = Jwts.parserBuilder()
                .setSigningKey(signingKey(jwtProperties.secret))
                .build()
                .parseClaimsJws(authorizationToken)
                .body

            val username: String = claims.subject

            @Suppress("UNCHECKED_CAST")
            val authoritiesClaim = claims["authorities"] as? List<String> ?: emptyList()
            val grantedAuthorities = authoritiesClaim.map { SimpleGrantedAuthority(it) }

            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(username, null, grantedAuthorities)
        }

        filterChain.doFilter(request, response)
    }
}
