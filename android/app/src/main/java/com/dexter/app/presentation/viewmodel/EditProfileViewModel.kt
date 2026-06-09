package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.ProfileSummary
import com.dexter.app.domain.repository.UserRepository
import com.dexter.app.data.remote.SearchRemoteDataSource
import com.dexter.app.presentation.ui.utils.InputFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EditProfileState {
    data object Loading : EditProfileState()
    data object Saved : EditProfileState()
    data class Error(val message: String) : EditProfileState()
}

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val searchRemoteDataSource: SearchRemoteDataSource
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

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    private val _skillsOffered = MutableStateFlow<List<String>>(emptyList())
    val skillsOffered = _skillsOffered.asStateFlow()

    private val _skillsSought = MutableStateFlow<List<String>>(emptyList())
    val skillsSought = _skillsSought.asStateFlow()

    private val _customSkills = MutableStateFlow<List<String>>(emptyList())
    val customSkills = _customSkills.asStateFlow()

    private var baseSkills = emptyList<String>()
    private val _availableSkills = MutableStateFlow<List<String>>(emptyList())
    val availableSkills = _availableSkills.asStateFlow()

    private val _availabilityDays = MutableStateFlow<Set<Int>>(emptySet())
    val availabilityDays = _availabilityDays.asStateFlow()

    private val _avatarBase64 = MutableStateFlow<String?>(null)
    val avatarBase64 = _avatarBase64.asStateFlow()

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
    fun onPhoneChange(value: String) { _phone.value = InputFilters.filterPhone(value) }
    fun onAvatarBase64Change(base64: String?) { _avatarBase64.value = base64 }

    fun toggleAvailabilityDay(dayOfWeek: Int) {
        _availabilityDays.value = if (dayOfWeek in _availabilityDays.value) {
            _availabilityDays.value - dayOfWeek
        } else {
            _availabilityDays.value + dayOfWeek
        }
    }

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

    fun addCustomSkill(skill: String, isOffered: Boolean) {
        val trimmed = InputFilters.filterSkill(skill).trim()
        if (trimmed.length < 2 || trimmed.length > 50) return
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

    fun loadFromProfile(profile: ProfileSummary) {
        _fullName.value = "${profile.firstName} ${profile.lastName}".trim()
        _bio.value = profile.bio
        _country.value = profile.country ?: ""
        _phone.value = profile.phone ?: ""
        _skillsOffered.value = profile.skillsOffered
        _skillsSought.value = profile.skillsSought
        _availabilityDays.value = profile.availabilityDays.toSet()

        initials = profile.initials
        firstName = profile.firstName
        lastName = profile.lastName
        avatarUrl = profile.avatarUrl
        rating = profile.rating
        totalSessions = profile.totalSessions
        totalMatches = profile.totalMatches
        reviews = profile.reviews

        loadAvailableSkills()
    }

    fun searchSkills(query: String) {
        viewModelScope.launch {
            searchRemoteDataSource.searchSkills(query)
                .onSuccess { dtos ->
                    val names = dtos.mapNotNull { it.name }
                    _availableSkills.value = (baseSkills + names).distinct()
                }
                .onFailure {
                    Log.e(TAG, "Error searching skills", it)
                }
        }
    }

    private fun loadAvailableSkills() {
        viewModelScope.launch {
            searchRemoteDataSource.getPopularSkills()
                .onSuccess { dtos ->
                    val names = dtos.mapNotNull { it.name }
                    baseSkills = names
                    _availableSkills.value = names
                }
                .onFailure {
                    Log.e(TAG, "Error loading available skills", it)
                }
        }
    }

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
                avatarUrl = _avatarBase64.value ?: avatarUrl,
                bio = _bio.value.trim(),
                country = _country.value.trim().ifBlank { null },
                phone = _phone.value.trim().ifBlank { null },
                rating = rating,
                totalSessions = totalSessions,
                totalMatches = totalMatches,
                skillsOffered = _skillsOffered.value,
                skillsSought = _skillsSought.value,
                availabilityDays = _availabilityDays.value.toList().sorted(),
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
