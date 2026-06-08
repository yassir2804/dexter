package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.R
import com.dexter.app.domain.model.User
import com.dexter.app.domain.repository.UserRepository
import com.dexter.app.presentation.ui.utils.InputFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * Sealed class representing the various states of the registration flow.
 */
sealed class RegisterState {
    /** No action taken yet */
    data object Idle : RegisterState()

    /** Registration in progress (spinner) */
    data object Loading : RegisterState()

    /** Registration completed successfully */
    data object Success : RegisterState()

    /** Validation or registration error */
    data class Error(@StringRes val messageRes: Int) : RegisterState()

    /** API error with a dynamic message (not a string resource) */
    data class ApiError(val message: String) : RegisterState()
}

/**
 * ViewModel shared between WelcomeScreen (step 1) and OnboardingScreen (step 2).
 *
 * Now uses @HiltViewModel to inject [UserRepository] and call
 * POST /auth/register via Retrofit instead of simulating with delay.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: UserRepository,
    private val searchRepository: com.dexter.app.domain.repository.SearchRepository
) : ViewModel() {

    companion object {
        private const val TAG = "RegisterViewModel"
        private const val MIN_PASSWORD_LENGTH = 6
    }

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state = _state.asStateFlow()

    // ── Welcome fields (step 1) ─────────────────────────────────

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible = _passwordVisible.asStateFlow()

    private val _confirmPasswordVisible = MutableStateFlow(false)
    val confirmPasswordVisible = _confirmPasswordVisible.asStateFlow()

    fun onEmailChange(value: String) { _email.value = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun onConfirmPasswordChange(value: String) { _confirmPassword.value = value }
    fun togglePasswordVisibility() { _passwordVisible.value = !_passwordVisible.value }
    fun toggleConfirmPasswordVisibility() { _confirmPasswordVisible.value = !_confirmPasswordVisible.value }

    /**
     * Validates Welcome fields before navigating to Onboarding.
     * Returns true if valid, sets Error state if not.
     */
    fun validateWelcomeFields(): Boolean {
        val emailVal = _email.value.trim()
        val passwordVal = _password.value
        val confirmVal = _confirmPassword.value

        return when {
            emailVal.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailVal).matches() -> {
                _state.value = RegisterState.Error(R.string.error_invalid_email)
                false
            }
            passwordVal.length < MIN_PASSWORD_LENGTH -> {
                _state.value = RegisterState.Error(R.string.error_password_short)
                false
            }
            passwordVal != confirmVal -> {
                _state.value = RegisterState.Error(R.string.error_password_mismatch)
                false
            }
            else -> {
                _state.value = RegisterState.Idle
                true
            }
        }
    }

    // ── Onboarding fields (step 2) ──────────────────────────────

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _country = MutableStateFlow("")
    val country = _country.asStateFlow()

    private val _skillsToTeach = MutableStateFlow<List<String>>(emptyList())
    val skillsToTeach = _skillsToTeach.asStateFlow()

    private val _skillsToLearn = MutableStateFlow<List<String>>(emptyList())
    val skillsToLearn = _skillsToLearn.asStateFlow()

    // Available skills loaded from backend
    private val _availableSkillsToTeach = MutableStateFlow<List<String>>(emptyList())
    val availableSkillsToTeach = _availableSkillsToTeach.asStateFlow()

    private val _availableSkillsToLearn = MutableStateFlow<List<String>>(emptyList())
    val availableSkillsToLearn = _availableSkillsToLearn.asStateFlow()

    // Custom skills added by user during registration
    private val _customSkills = MutableStateFlow<List<String>>(emptyList())
    val customSkills = _customSkills.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone = _phone.asStateFlow()

    private val _availabilityDays = MutableStateFlow<Set<Int>>(emptySet())
    val availabilityDays = _availabilityDays.asStateFlow()

    fun onNameChange(value: String) { _name.value = InputFilters.filterName(value) }
    fun onCountryChange(value: String) { _country.value = InputFilters.filterName(value) }
    fun onPhoneChange(value: String) { _phone.value = InputFilters.filterPhone(value) }

    fun toggleSkillToTeach(skill: String) {
        _skillsToTeach.value = if (skill in _skillsToTeach.value) {
            _skillsToTeach.value - skill
        } else {
            _skillsToTeach.value + skill
        }
    }

    fun toggleSkillToLearn(skill: String) {
        _skillsToLearn.value = if (skill in _skillsToLearn.value) {
            _skillsToLearn.value - skill
        } else {
            _skillsToLearn.value + skill
        }
    }

    fun toggleAvailabilityDay(dayOfWeek: Int) {
        _availabilityDays.value = if (dayOfWeek in _availabilityDays.value) {
            _availabilityDays.value - dayOfWeek
        } else {
            _availabilityDays.value + dayOfWeek
        }
    }

    /**
     * Adds a custom (user-created) skill and selects it for the target list.
     * @param skill The skill name to add.
     * @param isTeach true to add to teach list, false for learn list.
     */
    fun addCustomSkill(skill: String, isTeach: Boolean) {
        val trimmed = InputFilters.filterSkill(skill).trim()
        if (trimmed.length < 2) return
        val normalized = trimmed.lowercase()
        if (_customSkills.value.any { it.lowercase() == normalized }) return
        _customSkills.value = _customSkills.value + trimmed
        if (isTeach) {
            if (_skillsToTeach.value.none { it.lowercase() == normalized }) {
                _skillsToTeach.value = _skillsToTeach.value + trimmed
            }
        } else {
            if (_skillsToLearn.value.none { it.lowercase() == normalized }) {
                _skillsToLearn.value = _skillsToLearn.value + trimmed
            }
        }
    }

    /**
     * Loads popular skills from the backend to populate the skill picker.
     */
    fun loadAvailableSkills() {
        viewModelScope.launch {
            try {
                val result = searchRepository.getPopularSkills()
                result.onSuccess { skills ->
                    val skillNames = skills.map { it.name }.distinct()
                    // For now, use same list for both teach and learn
                    // In a real app, might want to fetch different categories
                    _availableSkillsToTeach.value = skillNames
                    _availableSkillsToLearn.value = skillNames
                }.onFailure { e ->
                    Log.e(TAG, "Failed to load available skills", e)
                    // Fallback to empty — picker will only show custom skills
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unknown error loading available skills", e)
            }
        }
    }

    // ── Registration — now calls the API via repository ─────────

    /**
     * Registers the user by calling POST /auth/register via the repository.
     *
     * [email] and [password] come from navigation arguments (collected in WelcomeScreen).
     * [onRegisterSuccess] is called after a successful API call to trigger navigation.
     */
    fun register(email: String, password: String, onRegisterSuccess: () -> Unit) {
        val nameTrimmed = _name.value.trim()
        if (nameTrimmed.isBlank()) {
            _state.value = RegisterState.Error(R.string.error_name_required)
            return
        }
        if (!InputFilters.isValidName(nameTrimmed)) {
            _state.value = RegisterState.Error(R.string.error_name_required)
            return
        }
        val phoneTrimmed = _phone.value.trim()
        if (phoneTrimmed.isNotBlank() && !InputFilters.isValidPhone(phoneTrimmed)) {
            _state.value = RegisterState.Error(R.string.error_invalid_phone)
            return
        }
        if (_skillsToTeach.value.isEmpty()) {
            _state.value = RegisterState.Error(R.string.error_skills_required)
            return
        }
        if (_skillsToLearn.value.isEmpty()) {
            _state.value = RegisterState.Error(R.string.error_skills_required)
            return
        }

        viewModelScope.launch {
            _state.value = RegisterState.Loading

            // Split "name" into firstName and lastName
            val nameParts = _name.value.trim().split(" ", limit = 2)
            val firstName = nameParts.first()
            val lastName = if (nameParts.size > 1) nameParts[1] else ""

            val user = User(
                id = 0L,
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName,
                username = email.substringBefore("@"),
                bio = null,
                avatarUrl = null,
                country = _country.value.trim().ifBlank { null },
                enabled = true,
                tokenExpired = false,
                rating = 0.0,
                totalSessions = 0,
                totalMatches = 0,
                createDate = Date(),
                updatedAt = Date(),
                phone = _phone.value.trim().ifBlank { null },
                skillsToTeach = _skillsToTeach.value,
                skillsToLearn = _skillsToLearn.value,
                availabilityDays = _availabilityDays.value.toList().sorted(),
            )

            repository.saveUser(user)
                .onSuccess { createdUser ->
                    Log.d(TAG, "Registration successful for $email, userId=${createdUser.id}")
                    onRegisterSuccess()
                }
                .onFailure { exception ->
                    Log.e(TAG, "Registration failed: ${exception.message}")
                    _state.value = RegisterState.ApiError(
                        exception.message ?: "Error desconocido"
                    )
                }
        }
    }

}
