package com.dexter.app.presentation.viewmodel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
}

/**
 * ViewModel shared between WelcomeScreen (step 1) and OnboardingScreen (step 2).
 *
 * Scoped to the auth_graph navigation subgraph so both screens share the same instance.
 * No repository dependency — registration is mocked with a delay.
 */
class RegisterViewModel : ViewModel() {

    companion object {
        private const val SIMULATED_DELAY_MS = 1500L
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

    fun onNameChange(value: String) { _name.value = value }
    fun onCountryChange(value: String) { _country.value = value }

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

    // ── Registration ────────────────────────────────────────────

    /**
     * Simulates user registration after validating onboarding fields.
     */
    fun register() {
        if (_name.value.trim().isBlank()) {
            _state.value = RegisterState.Error(R.string.error_name_required)
            return
        }

        viewModelScope.launch {
            _state.value = RegisterState.Loading
            delay(SIMULATED_DELAY_MS)
            _state.value = RegisterState.Success
        }
    }

    fun resetState() {
        _state.value = RegisterState.Idle
    }
}
