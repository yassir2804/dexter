package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.MatchDetail
import com.dexter.app.domain.repository.MatchRepository
import com.dexter.app.presentation.session.CurrentUserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MatchDetailState {
    data object Loading : MatchDetailState()
    data class Success(val match: MatchDetail) : MatchDetailState()
    data class Error(val message: String) : MatchDetailState()
}

sealed class MatchActionState {
    data object Idle : MatchActionState()
    data object Loading : MatchActionState()
    data object AcceptDone : MatchActionState()
    data object RejectDone : MatchActionState()
    data class Error(val message: String) : MatchActionState()
}

data class AcceptPickerData(
    val options: List<String>,
    val selected: String,
)

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    private val repository: MatchRepository
) : ViewModel() {

    companion object { private const val TAG = "MatchDetailViewModel" }

    private val _state = MutableStateFlow<MatchDetailState>(MatchDetailState.Loading)
    val state = _state.asStateFlow()

    private val _actionState = MutableStateFlow<MatchActionState>(MatchActionState.Idle)
    val actionState = _actionState.asStateFlow()

    private val _acceptPickerData = MutableStateFlow<AcceptPickerData?>(null)
    val acceptPickerData = _acceptPickerData.asStateFlow()

    fun loadMatch(matchId: Long) {
        viewModelScope.launch {
            _state.value = MatchDetailState.Loading
            val viewerUserId = CurrentUserSession.userId ?: 0L
            repository.getMatchDetail(matchId, viewerUserId)
                .onSuccess { _state.value = MatchDetailState.Success(it) }
                .onFailure { e ->
                    Log.e(TAG, "Error loading match detail: ${e.message}")
                    _state.value = MatchDetailState.Error(e.message ?: "Error desconocido")
                }
        }
    }

    fun onAcceptTapped(matchId: Long, viewerUserId: Long) {
        val match = (_state.value as? MatchDetailState.Success)?.match ?: run {
            doAccept(matchId, viewerUserId, null)
            return
        }

        // Only the receiver needs to pick; skip if initiatorSkill already assigned
        if (!match.isReceiver || match.initiatorSkill != null) {
            doAccept(matchId, viewerUserId, null)
            return
        }

        // Receiver picks from compatible skills first; if none overlap, from all offered
        val skillsToLearn = match.canLearnFromOther.ifEmpty { match.offeredSkills }

        if (skillsToLearn.isEmpty()) {
            doAccept(matchId, viewerUserId, null)
            return
        }

        if (skillsToLearn.size == 1) {
            doAccept(matchId, viewerUserId, skillsToLearn[0])
            return
        }

        // Multiple options — show picker
        _acceptPickerData.value = AcceptPickerData(
            options = skillsToLearn,
            selected = skillsToLearn.first()
        )
    }

    fun updateAcceptPickerSelection(skill: String) {
        _acceptPickerData.value = _acceptPickerData.value?.copy(selected = skill)
    }

    fun confirmAccept(matchId: Long, viewerUserId: Long) {
        val selected = _acceptPickerData.value?.selected
        _acceptPickerData.value = null
        doAccept(matchId, viewerUserId, selected)
    }

    fun dismissAcceptPicker() {
        _acceptPickerData.value = null
        _actionState.value = MatchActionState.Idle
    }

    private fun doAccept(matchId: Long, viewerUserId: Long, initiatorSkill: String?) {
        viewModelScope.launch {
            _actionState.value = MatchActionState.Loading
            val tz = runCatching { java.util.TimeZone.getDefault().id }.getOrNull()
            repository.acceptMatch(matchId, viewerUserId, initiatorSkill, tz)
                .onSuccess { _actionState.value = MatchActionState.AcceptDone }
                .onFailure { e ->
                    Log.e(TAG, "Error accepting match $matchId: ${e.message}")
                    _actionState.value = MatchActionState.Error(e.message ?: "Error al aceptar")
                }
        }
    }

    fun rejectMatch(matchId: Long) {
        viewModelScope.launch {
            _actionState.value = MatchActionState.Loading
            repository.rejectMatch(matchId)
                .onSuccess { _actionState.value = MatchActionState.RejectDone }
                .onFailure { e ->
                    Log.e(TAG, "Error rejecting match $matchId: ${e.message}")
                    _actionState.value = MatchActionState.Error(e.message ?: "Error al rechazar")
                }
        }
    }

    fun resetActionState() {
        _actionState.value = MatchActionState.Idle
    }
}
