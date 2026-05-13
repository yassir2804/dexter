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

data class SkillPickerData(
    val canTeach: List<String>,
    val canLearn: List<String>,
    val selectedTeach: String,
    val selectedLearn: String,
)

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

    private val _pickerData = MutableStateFlow<SkillPickerData?>(null)
    val pickerData = _pickerData.asStateFlow()

    private var currentUserProfile: ProfileSummary? = null
    private var pendingInitiatorId: Long = 0L
    private var pendingReceiverId: Long = 0L

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

    fun onConnectTapped(initiatorId: Long, receiverId: Long) {
        viewModelScope.launch {
            _connectState.value = ConnectState.Loading
            pendingInitiatorId = initiatorId
            pendingReceiverId = receiverId

            if (currentUserProfile == null) {
                repository.getProfileSummary(initiatorId)
                    .onSuccess { currentUserProfile = it }
                    .onFailure {
                        doInitiateMatch(null, null)
                        return@launch
                    }
            }

            val cu = currentUserProfile
            val otherProfile = (_state.value as? UserProfileState.Success)?.profile

            if (cu == null || otherProfile == null) {
                doInitiateMatch(null, null)
                return@launch
            }

            val currentOfferedLower = cu.skillsOffered.map { it.lowercase() }.toSet()
            val currentSoughtLower = cu.skillsSought.map { it.lowercase() }.toSet()

            val canTeach = otherProfile.skillsSought.filter { it.lowercase() in currentOfferedLower }
            val canLearn = otherProfile.skillsOffered.filter { it.lowercase() in currentSoughtLower }

            when {
                canTeach.isEmpty() && canLearn.isEmpty() -> doInitiateMatch(null, null)
                canTeach.size == 1 && canLearn.size == 1 -> doInitiateMatch(canTeach[0], canLearn[0])
                else -> {
                    _connectState.value = ConnectState.Idle
                    val teachOptions = canTeach.ifEmpty { cu.skillsOffered }
                    val learnOptions = canLearn.ifEmpty { otherProfile.skillsOffered }
                    _pickerData.value = SkillPickerData(
                        canTeach = teachOptions,
                        canLearn = learnOptions,
                        selectedTeach = teachOptions.first(),
                        selectedLearn = learnOptions.first(),
                    )
                }
            }
        }
    }

    fun updatePickerTeach(skill: String) {
        _pickerData.value = _pickerData.value?.copy(selectedTeach = skill)
    }

    fun updatePickerLearn(skill: String) {
        _pickerData.value = _pickerData.value?.copy(selectedLearn = skill)
    }

    fun confirmConnect() {
        val picker = _pickerData.value ?: return
        _pickerData.value = null
        viewModelScope.launch {
            doInitiateMatch(picker.selectedTeach, picker.selectedLearn)
        }
    }

    fun dismissPicker() {
        _pickerData.value = null
        _connectState.value = ConnectState.Idle
    }

    private suspend fun doInitiateMatch(initiatorSkill: String?, receiverSkill: String?) {
        _connectState.value = ConnectState.Loading
        matchRepository.initiateMatch(pendingInitiatorId, pendingReceiverId, initiatorSkill, receiverSkill)
            .onSuccess { _connectState.value = ConnectState.Done }
            .onFailure { e ->
                Log.e(TAG, "Error initiating match to $pendingReceiverId", e)
                _connectState.value = ConnectState.Error(e.message ?: "Error al conectar")
            }
    }
}
