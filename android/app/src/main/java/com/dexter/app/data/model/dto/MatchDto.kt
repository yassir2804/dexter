package com.dexter.app.data.model.dto

/**
 * LAB 3 - Data Transfer Object (DTO) para Match
 *
 * MatchDto es la clase que Gson usa para convertir JSON ↔ Kotlin.
 *
 * Cuando Mockoon envía un JSON como:
 * {
 *   "id": 1,
 *   "initiatorId": 123,
 *   "receiverId": 456,
 *   "compatibilityScore": 92,
 *   "status": "ACCEPTED",
 *   "createdAt": "2026-03-25T10:30:00Z"
 * }
 *
 * Gson automáticamente crea un MatchDto con esos valores.
 *
 * Nota: Los nombres de las propiedades deben coincidir EXACTAMENTE con las claves JSON
 * (o usar la anotación @SerializedName para mapeo personalizado).
 *
 * DTO vs Entity:
 * - DTO: representa cómo el SERVIDOR envía los datos (estructura externa)
 * - Entity: representa cómo TU APP maneja los datos internamente (estructura interna)
 * - En el futuro crearemos data classes Entity y convertiremos DTO → Entity
 */
data class MatchDto(
    val id: Long,
    val initiatorId: Long,
    val receiverId: Long,
    val compatibilityScore: Int,
    val status: String,            // "PENDING", "ACCEPTED", "REJECTED", "CANCELLED"
    val createdAt: String          // Timestamp ISO 8601 ej: "2026-03-25T10:30:00Z"
)
