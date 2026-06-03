package com.dexter.app.data.util

/**
 * LAB 3 - Result<T> Sealed Class (Error Handling Pattern)
 *
 * Este tipo envuelve CUALQUIER resultado (éxito o error) en un contenedor seguro.
 * Así evitamos que las excepciones nos derriben la app.
 *
 * Sealed class = solo puede ser subclasificada dentro del mismo archivo
 * (Result solo puede ser Success o Error, nada más)
 *
 * Ejemplo de uso:
 *
 *   val result: Result<List<MatchDto>> = remoteDataSource.getMatches(1)
 *
 *   when (result) {
 *       is Result.Success -> {
 *           val matches = result.data
 *           println("Matches obtenidos: ${matches.size}")
 *       }
 *       is Result.Error -> {
 *           val error = result.exception
 *           println("Error: ${error.message}")
 *       }
 *   }
 *
 * Ventajas:
 * 1. Nunca sorpresas (excepciones inesperadas)
 * 2. El compilador te obliga a manejar AMBOS casos (Success y Error)
 * 3. El código queda más limpio sin try-catch anidados
 * 4. Funciona bien con Coroutines
 */
sealed class Result<out T> {

    /**
     * Success: el resultado exitoso con los datos
     *
     * @param data T → el valor retornado (puede ser Unit, String, List<Match>, lo que sea)
     * @param out T → covariante, permite Result<String> asignarse a Result<Any>
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Error: algo salió mal
     *
     * @param exception Exception → la excepción capturada
     *
     * Puede ser:
     * - IOException: sin conexión a internet
     * - JsonSyntaxException: el JSON del servidor está mal formado
     * - HttpException: el servidor respondió con 404, 500, etc.
     * - Cualquier otra Exception que ocurra
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Función helper: verifica si es Success
     * Útil para código conciso:
     *
     *   if (result.isSuccess) {
     *       val data = (result as Success).data
     *   }
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * Función helper: verifica si es Error
     */
    val isError: Boolean
        get() = this is Error

    /**
     * Función helper: obtiene los datos si es Success, null si Error
     * Uso: val matches = result.getOrNull()  // null si error
     */
    fun getOrNull(): T? = when (this) {
        is Success -> this.data
        is Error -> null
    }

    /**
     * Función helper: obtiene la excepción si es Error, null si Success
     * Uso: val error = result.exceptionOrNull()  // null si éxito
     */
    fun exceptionOrNull(): Exception? = when (this) {
        is Success -> null
        is Error -> this.exception
    }
}
