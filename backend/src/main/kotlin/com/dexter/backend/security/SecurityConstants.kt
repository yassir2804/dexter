package com.dexter.backend.security

/**
 * Constantes no sensibles relacionadas al transporte del JWT.
 * El secret, issuer, audience y expiration viven en JwtProperties
 * (cargados desde application.properties).
 */
object SecurityConstants {
    const val TOKEN_TYPE = "JWT"
    const val TOKEN_PREFIX = "Bearer "
    const val APPLICATION_JSON = "application/json"
    const val UTF_8 = "UTF-8"
}
