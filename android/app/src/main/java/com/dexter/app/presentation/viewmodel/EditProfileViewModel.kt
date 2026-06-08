package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.ProfileSummary
import com.dexter.app.domain.repository.UserRepository
import com.dexter.app.presentation.ui.utils.InputFilters
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

    private val _skillsSought = MutableStateFlow<List<String>>(emptyList())
    val skillsSought = _skillsSought.asStateFlow()

    // Custom skills added by the user in this session (not in predefined list)
    private val _customSkills = MutableStateFlow<List<String>>(emptyList())
    val customSkills = _customSkills.asStateFlow()

    // Non-editable fields kept for building the ProfileSummary
    private var initials = ""
    private var firstName = ""
    private var lastName = ""
    private var avatarUrl: String? = null
    private var rating = 0.0
    private var totalSessions = 0
    private var totalMatches = 0
    private var reviews = emptyList<com.dexter.app.domain.model.ProfileReviewItem>()

    fun onFullNameChange(value: String) { _fullName.value = InputFilters.filterName(value) }
    fun onBioChange(value: String) { _bio.value = InputFilters.filterSafeText(value) }
    fun onCountryChange(value: String) { _country.value = InputFilters.filterName(value) }

    fun toggleSkillOffered(skill: String) {
        _skillsOffered.value = if (skill in _skillsOffered.value) {
            _skillsOffered.value - skill
        } else {
            _skillsOffered.value + skill
        }
    }

    fun toggleSkillSought(skill: String) {
        _skillsSought.value = if (skill in _skillsSought.value) {
            _skillsSought.value - skill
        } else {
            _skillsSought.value + skill
        }
    }

    /**
     * Adds a custom (user-created) skill and selects it for the target list.
     * @param isOffered true to add to offered list, false for sought list.
     */
    fun addCustomSkill(skill: String, isOffered: Boolean) {
        val trimmed = InputFilters.filterSkill(skill).trim()
        if (trimmed.length < 2) return
        val normalized = trimmed.lowercase()
        if (_customSkills.value.any { it.lowercase() == normalized }) return
        _customSkills.value = _customSkills.value + trimmed
        if (isOffered) {
            if (_skillsOffered.value.none { it.lowercase() == normalized }) {
                _skillsOffered.value = _skillsOffered.value + trimmed
            }
        } else {
            if (_skillsSought.value.none { it.lowercase() == normalized }) {
                _skillsSought.value = _skillsSought.value + trimmed
            }
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
        _skillsSought.value = profile.skillsSought

        // Store non-editable fields for rebuilding the ProfileSummary
        initials = profile.initials
        firstName = profile.firstName
        lastName = profile.lastName
        avatarUrl = profile.avatarUrl
        rating = profile.rating
        totalSessions = profile.totalSessions
        totalMatches = profile.totalMatches
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
                skillsSought = _skillsSought.value,
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
