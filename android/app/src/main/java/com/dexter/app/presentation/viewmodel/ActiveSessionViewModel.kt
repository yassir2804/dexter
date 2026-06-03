package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.Session
import com.dexter.app.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ActiveSessionState {
    data object Loading : ActiveSessionState()
    data class Active(val session: Session, val notes: String, val ending: Boolean) : ActiveSessionState()
    data object Ended : ActiveSessionState()
    data class Error(val message: String) : ActiveSessionState()
}

@HiltViewModel
class ActiveSessionViewModel @Inject constructor(
    private val repository: SessionRepository
) : ViewModel() {

    companion object { private const val TAG = "ActiveSessionViewModel" }

    private val _state = MutableStateFlow<ActiveSessionState>(ActiveSessionState.Loading)
    val state = _state.asStateFlow()

    fun loadSession(sessionId: Long) {
        viewModelScope.launch {
            _state.value = ActiveSessionState.Loading
            repository.getActiveSession(sessionId)
                .onSuccess { session ->
                    _state.value = ActiveSessionState.Active(
                        session = session,
                        notes = "",
                        ending = false
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading active session", exception)
                    _state.value = ActiveSessionState.Error(
                        exception.message ?: "Error desconocido"
                    )
                }
        }
    }

    fun onNotesChange(value: String) {
        val current = _state.value as? ActiveSessionState.Active ?: return
        _state.value = current.copy(notes = value)
    }

    fun onEndSession() {
        val current = _state.value as? ActiveSessionState.Active ?: return
        viewModelScope.launch {
            _state.value = current.copy(ending = true)
            repository.completeSession(current.session.id, current.notes)
                .onSuccess { _state.value = ActiveSessionState.Ended }
                .onFailure { exception ->
                    Log.e(TAG, "Error ending session", exception)
                    _state.value = current.copy(ending = false)
                }
        }
    }
}
