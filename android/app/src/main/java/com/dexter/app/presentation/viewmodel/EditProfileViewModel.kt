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

/**
 * UI state for the Edit Profile screen.
 * Follows the same Loading / Success / Empty / Error pattern as [HomeState].
 */
sealed class EditProfileState {
    data object Loading : EditProfileState()
    data object Saved : EditProfileState()
    data class Error(val message: String) : EditProfileState()
}

/**
 * ViewModel for the Edit Profile screen.
 *
 * Exposes editable fields as [MutableStateFlow]s that the UI binds to.
 * [saveProfile] sends a PUT /profile-summary via the repository.
 */
@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "EditProfileViewModel"
    }

    private val _state = MutableStateFlow<EditProfileState?>(null)
    val state = _state.asStateFlow()

    // ── Editable fields ─────────────────────────────────────────

    private val _fullName = MutableStateFlow("")
    val fullName = _fullName.asStateFlow()

    private val _bio = MutableStateFlow("")
    val bio = _bio.asStateFlow()

    private val _country = MutableStateFlow("")
    val country = _country.asStateFlow()

    private val _skillsOffered = MutableStateFlow<List<String>>(emptyList())
    val skillsOffered = _skillsOffered.asStateFlow()

    // Non-editable fields kept for building the ProfileSummary
    private var initials = ""
    private var firstName = ""
    private var lastName = ""
    private var avatarUrl: String? = null
    private var rating = 0.0
    private var totalSessions = 0
    private var totalMatches = 0
    private var skillsSought = emptyList<String>()
    private var reviews = emptyList<com.dexter.app.domain.model.ProfileReviewItem>()

    fun onFullNameChange(value: String) { _fullName.value = value }
    fun onBioChange(value: String) { _bio.value = value }
    fun onCountryChange(value: String) { _country.value = value }

    fun toggleSkillOffered(skill: String) {
        _skillsOffered.value = if (skill in _skillsOffered.value) {
            _skillsOffered.value - skill
        } else {
            _skillsOffered.value + skill
        }
    }

    /**
     * Loads the current profile data into the editable fields.
     * Called once from LaunchedEffect when the screen opens.
     */
    fun loadFromProfile(profile: ProfileSummary) {
        _fullName.value = "${profile.firstName} ${profile.lastName}".trim()
        _bio.value = profile.bio
        _country.value = profile.country ?: ""
        _skillsOffered.value = profile.skillsOffered

        // Store non-editable fields for rebuilding the ProfileSummary
        initials = profile.initials
        firstName = profile.firstName
        lastName = profile.lastName
        avatarUrl = profile.avatarUrl
        rating = profile.rating
        totalSessions = profile.totalSessions
        totalMatches = profile.totalMatches
        skillsSought = profile.skillsSought
        reviews = profile.reviews
    }

    /**
     * Sends PUT /profile-summary with the edited data.
     * On success calls [onSaved] to navigate back.
     */
    fun saveProfile(onSaved: () -> Unit) {
        viewModelScope.launch {
            _state.value = EditProfileState.Loading

            val nameParts = _fullName.value.trim().split(" ", limit = 2)
            val newFirstName = nameParts.first()
            val newLastName = if (nameParts.size > 1) nameParts[1] else lastName
            val newInitials = "${newFirstName.firstOrNull() ?: ""}${newLastName.firstOrNull() ?: ""}".uppercase()

            val updatedProfile = ProfileSummary(
                initials = newInitials,
                firstName = newFirstName,
                lastName = newLastName,
                avatarUrl = avatarUrl,
                bio = _bio.value.trim(),
                country = _country.value.trim().ifBlank { null },
                rating = rating,
                totalSessions = totalSessions,
                totalMatches = totalMatches,
                skillsOffered = _skillsOffered.value,
                skillsSought = skillsSought,
                reviews = reviews
            )

            repository.updateProfile(updatedProfile)
                .onSuccess {
                    Log.d(TAG, "Profile updated successfully")
                    onSaved()
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error updating profile: ${exception.message}")
                    _state.value = EditProfileState.Error(
                        exception.message ?: "Error desconocido"
                    )
                }
        }
    }
}
