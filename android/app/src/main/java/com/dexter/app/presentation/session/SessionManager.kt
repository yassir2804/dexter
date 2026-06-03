package com.dexter.app.presentation.session

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralises the app's session lifecycle.
 *
 * Every piece of state that must be wiped on logout lives here so we don't
 * forget to clear something when auth is extended later (e.g. tokens,
 * DataStore, cached repos).
 *
 * Hilt provides this automatically thanks to [Inject] + [Singleton].
 */
@Singleton
class SessionManager @Inject constructor() {

    companion object { private const val TAG = "SessionManager" }

    fun login(userId: Long) {
        CurrentUserSession.userId = userId
    }

    fun logout() {
        Log.i(TAG, "Logout — wiping session state")
        CurrentUserSession.clear()
        ThemePreference.clear()
        // Future: DataStore.edit { it.clear() }, cached repos, Firebase sign-out…
    }

    fun isLoggedIn(): Boolean = CurrentUserSession.userId != null
}
