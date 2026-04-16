package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.ProfileSummary
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

/**
 * Loads another user's profile (distinct from [ProfileViewModel] which
 * loads the current user). Reuses [UserRepository.getProfileSummary] —
 * the API already exists.
 */
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    companion object { private const val TAG = "UserProfileViewModel" }

    private val _state = MutableStateFlow<UserProfileState>(UserProfileState.Loading)
    val state = _state.asStateFlow()

    fun loadProfile(userId: Long) {
        viewModelScope.launch {
            _state.value = UserProfileState.Loading
            repository.getProfileSummary(userId)
                .onSuccess { profile ->
                    _state.value = UserProfileState.Success(profile)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading user profile", exception)
                    _state.value = UserProfileState.Error(
                        exception.message ?: "Error desconocido"
                    )
                }
        }
    }
}
