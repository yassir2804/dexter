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

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Success(val profile: ProfileSummary) : ProfileState()
    data object Empty : ProfileState()
    data class Error(val message: String) : ProfileState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Empty)
    val profileState = _profileState.asStateFlow()

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    fun loadProfile(userId: Long) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            repository.getProfileSummary(userId)
                .onSuccess { profile ->
                    _profileState.value = ProfileState.Success(profile)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading profile: ${exception.message}")
                    _profileState.value = ProfileState.Error(exception.message ?: "Error desconocido")
                }
        }
    }

    /**
     * Sends a PUT /profile-summary to update the user's profile.
     * On success, updates the local state with the server response.
     */
    fun updateProfile(profile: ProfileSummary) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            repository.updateProfile(profile)
                .onSuccess { updatedProfile ->
                    Log.d(TAG, "Profile updated successfully")
                    _profileState.value = ProfileState.Success(updatedProfile)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error updating profile: ${exception.message}")
                    _profileState.value = ProfileState.Error(exception.message ?: "Error desconocido")
                }
        }
    }
}
