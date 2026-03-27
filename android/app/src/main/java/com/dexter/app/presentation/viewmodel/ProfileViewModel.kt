package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.ProfileSummary
import com.dexter.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Success(val profile: ProfileSummary) : ProfileState()
    data object Empty : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Empty)
    val profileState = _profileState.asStateFlow()

    fun loadProfile(userId: Long) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading

            repository.getProfileSummary(userId)
                .onSuccess { profile ->
                    _profileState.value = ProfileState.Success(profile)
                }
                .onFailure { exception ->
                    Log.e("ProfileViewModel", "Error loading profile: ${exception.message}")
                    _profileState.value = ProfileState.Error(exception.message ?: "Error desconocido")
                }
        }
    }
}
