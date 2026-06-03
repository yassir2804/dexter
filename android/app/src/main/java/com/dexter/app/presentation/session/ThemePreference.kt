package com.dexter.app.presentation.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global theme preference singleton.
 *
 * null = follow system, true = dark, false = light.
 * Uses StateFlow so Compose recomposes when the value changes.
 */
object ThemePreference {
    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode = _isDarkMode.asStateFlow()

    fun toggle(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun clear() {
        _isDarkMode.value = null
    }
}
