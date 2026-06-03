package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.data.util.TokenStore
import com.dexter.app.domain.repository.UserRepository
import com.dexter.app.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Encapsula los flujos del SettingsScreen:
 *  - Logout (limpia token + session manager)
 *  - Estado y toggle del "Perfil visible" persistido en el backend
 *
 * El cliente carga el estado inicial desde /profile-summary la primera
 * vez que se abre Settings. Cada toggle dispara un PUT al backend para
 * actualizar `profileVisible`.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val tokenStore: TokenStore,
    private val userRepository: UserRepository,
) : ViewModel() {

    companion object { private const val TAG = "SettingsViewModel" }

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut = _loggedOut.asStateFlow()

    private val _profileVisible = MutableStateFlow(true)
    val profileVisible = _profileVisible.asStateFlow()

    init {
        loadProfileVisible()
    }

    private fun loadProfileVisible() {
        viewModelScope.launch {
            val userId = tokenStore.getUserId() ?: return@launch
            userRepository.getProfileSummary(userId)
                .onSuccess { _profileVisible.value = it.profileVisible }
                .onFailure { Log.w(TAG, "No pude cargar profileVisible: ${it.message}") }
        }
    }

    /** Toggle optimista: actualiza UI inmediato y revierte si falla el PUT. */
    fun setProfileVisible(visible: Boolean) {
        val previous = _profileVisible.value
        _profileVisible.value = visible
        viewModelScope.launch {
            val userId = tokenStore.getUserId()
            if (userId == null) {
                _profileVisible.value = previous
                return@launch
            }
            userRepository.getProfileSummary(userId)
                .onSuccess { current ->
                    userRepository.updateProfile(current.copy(profileVisible = visible))
                        .onFailure {
                            Log.w(TAG, "PUT profile fallo: ${it.message}")
                            _profileVisible.value = previous
                        }
                }
                .onFailure {
                    Log.w(TAG, "GET profile fallo: ${it.message}")
                    _profileVisible.value = previous
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenStore.clearToken()
            sessionManager.logout()
            _loggedOut.value = true
        }
    }
}
