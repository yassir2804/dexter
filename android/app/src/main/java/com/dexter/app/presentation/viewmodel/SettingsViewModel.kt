package com.dexter.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.data.util.TokenStore
import com.dexter.app.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Encapsulates the logout flow so [SettingsScreen] no longer runs
 * session-cleanup logic inline inside NavGraph.
 *
 * Exposes [loggedOut] as a one-shot flag — the Screen observes it
 * with LaunchedEffect and navigates to Login when it flips to true.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val tokenStore: TokenStore,
) : ViewModel() {

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut = _loggedOut.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            tokenStore.clearToken()
            sessionManager.logout()
            _loggedOut.value = true
        }
    }
}
