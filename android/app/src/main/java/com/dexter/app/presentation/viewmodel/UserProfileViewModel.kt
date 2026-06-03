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

/** Picker shown when A has multiple skills they could learn from B. */
data class SkillPickerData(
    val canLearn: List<String>,
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

    /**
     * Called when the user taps "Conectar".
     *
     * [searchedSkill] — the skill A searched for to find this person (= what A wants to learn).
     * Used as [receiverSkill] when no compatible skill is found in the profiles.
     *
     * Logic:
     *  - No compatible skills on either side → send immediately with receiverSkill = searchedSkill
     *  - Exactly 1 on each side → auto-assign both
     *  - canLearn == 1 (any canTeach) → auto-pick receiverSkill; B picks initiatorSkill when accepting
     *  - canLearn > 1 → show picker so A selects what they want to learn; B picks later
     */
    fun onConnectTapped(initiatorId: Long, receiverId: Long, searchedSkill: String? = null) {
        viewModelScope.launch {
            _connectState.value = ConnectState.Loading
            pendingInitiatorId = initiatorId
            pendingReceiverId = receiverId

            if (currentUserProfile == null) {
                repository.getProfileSummary(initiatorId)
                    .onSuccess { currentUserProfile = it }
                    .onFailure {
                        doInitiateMatch(null, searchedSkill)
                        return@launch
                    }
            }

            val cu = currentUserProfile
            val otherProfile = (_state.value as? UserProfileState.Success)?.profile

            if (cu == null || otherProfile == null) {
                doInitiateMatch(null, searchedSkill)
                return@launch
            }

            val currentOfferedLower = cu.skillsOffered.map { it.lowercase() }.toSet()
            val currentSoughtLower = cu.skillsSought.map { it.lowercase() }.toSet()

            val canTeach = otherProfile.skillsSought.filter { it.lowercase() in currentOfferedLower }
            val canLearn = otherProfile.skillsOffered.filter { it.lowercase() in currentSoughtLower }

            when {
                // No compatible skills at all — use the skill A searched for
                canLearn.isEmpty() && canTeach.isEmpty() ->
                    doInitiateMatch(null, searchedSkill)

                // Exactly 1 on both sides — auto-assign
                canLearn.size == 1 && canTeach.size == 1 ->
                    doInitiateMatch(canTeach[0], canLearn[0])

                // Only 1 learn option — auto-pick; B will choose initiatorSkill when accepting
                canLearn.size == 1 ->
                    doInitiateMatch(null, canLearn[0])

                // No learn options (but A can teach something) — use searchedSkill as fallback
                canLearn.isEmpty() ->
                    doInitiateMatch(null, searchedSkill)

                // Multiple learn options — show picker so A chooses what they want to learn
                else -> {
                    _connectState.value = ConnectState.Idle
                    _pickerData.value = SkillPickerData(
                        canLearn = canLearn,
                        selectedLearn = canLearn.first()
                    )
                }
            }
        }
    }

    fun updatePickerLearn(skill: String) {
        _pickerData.value = _pickerData.value?.copy(selectedLearn = skill)
    }

    fun confirmConnect() {
        val picker = _pickerData.value ?: return
        _pickerData.value = null
        viewModelScope.launch {
            doInitiateMatch(null, picker.selectedLearn)
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
