package com.dexter.backend.security

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuracion del JWT cargada desde application.properties (`jwt.*`).
 *
 * Mantener el secret fuera del codigo fuente; en produccion debe venir
 * de una variable de entorno o de un secret manager.
 */
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    var secret: String = "",
    var issuer: String = "secure-api",
    var audience: String = "secure-app",
    var expiration: Long = 86_400_000,
)
