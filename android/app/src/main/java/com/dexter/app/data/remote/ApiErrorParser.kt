package com.dexter.app.data.remote

import org.json.JSONObject

/**
 * Convierte la respuesta de error del backend en un mensaje legible para el usuario.
 *
 * El backend (Spring Boot) responde los errores con esta forma:
 * ```
 * {
 *   "status": "400 BAD_REQUEST",
 *   "message": "error occurred",
 *   "debugMessage": "El telefono no puede superar 20 caracteres",
 *   "apiSubErrors": [ { "code": "...", "message": "..." } ]
 * }
 * ```
 *
 * Se prioriza el mensaje MÁS específico disponible (el del backend). Si el cuerpo no
 * se puede interpretar, se devuelve un mensaje claro segun el codigo HTTP — nunca el
 * JSON crudo ni un "API error 400" tecnico.
 */
object ApiErrorParser {

    /** Mensaje listo para mostrar al usuario a partir del codigo y el cuerpo de error. */
    fun toMessage(code: Int, errorBody: String?): String =
        parseBackendMessage(errorBody) ?: genericForCode(code)

    /** Extrae el mensaje específico que envió el backend, o null si no hay. */
    private fun parseBackendMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val json = JSONObject(errorBody)

            // 1) Primer sub-error (errores de validación de campos: longitud, formato…).
            val subErrors = json.optJSONArray("apiSubErrors")
            if (subErrors != null && subErrors.length() > 0) {
                val msg = subErrors.optJSONObject(0)?.optString("message").orEmpty()
                if (msg.isNotBlank() && msg != "null") return msg
            }

            // 2) debugMessage suele traer el detalle real del problema.
            json.optString("debugMessage").takeIf { it.isNotBlank() && it != "null" }
                ?.let { return it }

            // 3) message general (descartando el placeholder genérico de Spring).
            json.optString("message")
                .takeIf { it.isNotBlank() && it != "null" && it != "error occurred" }
                ?.let { return it }

            null
        } catch (e: Exception) {
            // No era JSON: si es un texto corto y no HTML, úsalo tal cual.
            errorBody.takeIf { it.length in 1..200 && !it.trimStart().startsWith("<") }
        }
    }

    /** Mensaje amigable por defecto cuando el backend no envió un detalle utilizable. */
    private fun genericForCode(code: Int): String = when (code) {
        400 -> "Datos inválidos. Revisa la información ingresada."
        401 -> "Tu sesión expiró. Inicia sesión nuevamente."
        403 -> "No tienes permiso para realizar esta acción."
        404 -> "No se encontró el recurso solicitado."
        409 -> "El recurso ya existe o está en uso."
        413, 422 -> "Algún dato no es válido o es demasiado largo."
        in 500..599 -> "Error del servidor. Intenta de nuevo más tarde."
        else -> "Ocurrió un error (código $code). Intenta de nuevo."
    }
}
