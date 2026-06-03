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
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

sealed class EditSessionState {
    data object Loading : EditSessionState()
    data class Editing(
        val session: Session,
        val title: String,
        val description: String,
        val date: String,
        val time: String,
        val duration: String,
        val showDeleteConfirm: Boolean,
        val saving: Boolean,
        val deleting: Boolean
    ) : EditSessionState()
    data object Saved : EditSessionState()
    data object Cancelled : EditSessionState()
    data class Error(val message: String) : EditSessionState()
}

/**
 * Edits a session's metadata (title/description/date/time/duration)
 * and supports cancelling the session with a confirmation dialog.
 */
@HiltViewModel
class EditSessionViewModel @Inject constructor(
    private val repository: SessionRepository
) : ViewModel() {

    companion object {
        private const val TAG = "EditSessionViewModel"
        private const val DEFAULT_DURATION = "60"
    }

    private val _state = MutableStateFlow<EditSessionState>(EditSessionState.Loading)
    val state = _state.asStateFlow()

    private val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun loadSession(sessionId: Long) {
        viewModelScope.launch {
            _state.value = EditSessionState.Loading
            repository.getSessionById(sessionId)
                .onSuccess { session ->
                    _state.value = EditSessionState.Editing(
                        session = session,
                        title = session.title,
                        description = session.description,
                        date = session.scheduledAt?.let { dateFmt.format(it) } ?: "",
                        time = session.scheduledAt?.let { timeFmt.format(it) } ?: "",
                        duration = DEFAULT_DURATION,
                        showDeleteConfirm = false,
                        saving = false,
                        deleting = false
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading session", exception)
                    _state.value = EditSessionState.Error(
                        exception.message ?: "Error desconocido"
                    )
                }
        }
    }

    private fun currentEditing(): EditSessionState.Editing? =
        _state.value as? EditSessionState.Editing

    fun onTitleChange(value: String) {
        currentEditing()?.let { _state.value = it.copy(title = value) }
    }

    fun onDescriptionChange(value: String) {
        currentEditing()?.let { _state.value = it.copy(description = value) }
    }

    fun onDateChange(value: String) {
        currentEditing()?.let { _state.value = it.copy(date = value) }
    }

    fun onTimeChange(value: String) {
        currentEditing()?.let { _state.value = it.copy(time = value) }
    }

    fun onDurationChange(value: String) {
        currentEditing()?.let { _state.value = it.copy(duration = value) }
    }

    fun onRequestDelete() {
        currentEditing()?.let { _state.value = it.copy(showDeleteConfirm = true) }
    }

    fun onDismissDelete() {
        currentEditing()?.let { _state.value = it.copy(showDeleteConfirm = false) }
    }

    fun onSave() {
        val editing = currentEditing() ?: return
        viewModelScope.launch {
            _state.value = editing.copy(saving = true)
            val updated = editing.session.copy(
                title = editing.title,
                description = editing.description
            )
            repository.updateSession(updated)
                .onSuccess { _state.value = EditSessionState.Saved }
                .onFailure { exception ->
                    Log.e(TAG, "Error saving session", exception)
                    _state.value = editing.copy(saving = false)
                }
        }
    }

    fun onConfirmDelete() {
        val editing = currentEditing() ?: return
        viewModelScope.launch {
            _state.value = editing.copy(deleting = true)
            repository.cancelSession(editing.session.id)
                .onSuccess { _state.value = EditSessionState.Cancelled }
                .onFailure { exception ->
                    Log.e(TAG, "Error cancelling session", exception)
                    _state.value = editing.copy(deleting = false, showDeleteConfirm = false)
                }
        }
    }
}
