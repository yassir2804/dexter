package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.ProfileSummary
import com.dexter.app.domain.repository.MatchRepository
import com.dexter.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserProfileState {
    data object Loading : UserProfileState()
    data class Success(val profile: ProfileSummary) : UserProfileState()
    data object Empty : UserProfileState()
    data class Error(val message: String) : UserProfileState()
}

sealed class ConnectState {
    data object Idle : ConnectState()
    data object Loading : ConnectState()
    data object Done : ConnectState()
    data class Error(val message: String) : ConnectState()
}

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val matchRepository: MatchRepository
) : ViewModel() {

    companion object { private const val TAG = "UserProfileViewModel" }

    private val _state = MutableStateFlow<UserProfileState>(UserProfileState.Loading)
    val state = _state.asStateFlow()

    private val _connectState = MutableStateFlow<ConnectState>(ConnectState.Idle)
    val connectState = _connectState.asStateFlow()

    fun loadProfile(userId: Long) {
        viewModelScope.launch {
            _state.value = UserProfileState.Loading
            repository.getProfileSummary(userId)
                .onSuccess { _state.value = UserProfileState.Success(it) }
                .onFailure { e ->
                    Log.e(TAG, "Error loading user profile", e)
                    _state.value = UserProfileState.Error(e.message ?: "Error desconocido")
                }
        }
    }

    fun initiateMatch(initiatorId: Long, receiverId: Long) {
        viewModelScope.launch {
            _connectState.value = ConnectState.Loading
            matchRepository.initiateMatch(initiatorId, receiverId)
                .onSuccess { _connectState.value = ConnectState.Done }
                .onFailure { e ->
                    Log.e(TAG, "Error initiating match to $receiverId", e)
                    _connectState.value = ConnectState.Error(e.message ?: "Error al conectar")
                }
        }
    }
}
