package com.dexter.app.presentation.session

/**
 * Simple singleton that holds the authenticated user's ID.
 *
 * Set after a successful login in NavGraph and consumed by
 * ViewModels that need the current user (e.g. ProfileViewModel).
 *
 * Temporary solution — will be replaced by a backend-backed
 * SessionManager once the API is integrated.
 */
object CurrentUserSession {
    var userId: Long? = null
    var userRole: String? = null

    fun isAdmin(): Boolean = userRole == "ROLE_ADMIN"

    fun clear() {
        userId = null
        userRole = null
    }
}
