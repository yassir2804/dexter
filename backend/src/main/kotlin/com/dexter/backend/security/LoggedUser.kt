package com.dexter.backend.security

/**
 * Guarda el email del usuario autenticado en un ThreadLocal para
 * que los Service puedan leerlo sin tener que recibir el Principal.
 */
object LoggedUser {
    private val userHolder = ThreadLocal<String>()

    fun logIn(user: String) {
        userHolder.set(user)
    }

    fun logOut() {
        userHolder.remove()
    }

    fun get(): String? = userHolder.get()
}
