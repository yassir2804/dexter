package com.dexter.app.presentation.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Preferencias de notificaciones por tipo.
 * Persiste en memoria durante la sesión (mismo patrón que ThemePreference).
 */
object NotificationPreference {
    private val _showMatches = MutableStateFlow(true)
    val showMatches = _showMatches.asStateFlow()

    private val _showReminders = MutableStateFlow(true)
    val showReminders = _showReminders.asStateFlow()

    private val _showRatings = MutableStateFlow(true)
    val showRatings = _showRatings.asStateFlow()

    fun setShowMatches(enabled: Boolean) { _showMatches.value = enabled }
    fun setShowReminders(enabled: Boolean) { _showReminders.value = enabled }
    fun setShowRatings(enabled: Boolean) { _showRatings.value = enabled }
}
