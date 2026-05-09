package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.MatchDetail
import com.dexter.app.domain.repository.MatchRepository
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

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    private val repository: MatchRepository
) : ViewModel() {

    companion object { private const val TAG = "MatchDetailViewModel" }

    private val _state = MutableStateFlow<MatchDetailState>(MatchDetailState.Loading)
    val state = _state.asStateFlow()

    private val _actionState = MutableStateFlow<MatchActionState>(MatchActionState.Idle)
    val actionState = _actionState.asStateFlow()

    fun loadMatch(matchId: Long) {
        viewModelScope.launch {
            _state.value = MatchDetailState.Loading
            repository.getMatchDetail(matchId)
                .onSuccess { _state.value = MatchDetailState.Success(it) }
                .onFailure { e ->
                    Log.e(TAG, "Error loading match detail: ${e.message}")
                    _state.value = MatchDetailState.Error(e.message ?: "Error desconocido")
                }
        }
    }

    fun acceptMatch(matchId: Long, viewerUserId: Long) {
        viewModelScope.launch {
            _actionState.value = MatchActionState.Loading
            repository.acceptMatch(matchId, viewerUserId)
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
}
