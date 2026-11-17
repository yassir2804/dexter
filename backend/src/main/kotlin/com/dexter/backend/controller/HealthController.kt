package com.dexter.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Endpoint público (sin JWT) para smoke tests y verificación de conectividad.
 * La ruta está permitAll() en JwtSecurityConfiguration.
 */
@RestController
class HealthController {

    @GetMapping("\${url.health}")
    fun health(): Map<String, String> = mapOf(
        "status" to "ok",
        "environment" to "dev",
    )
}
