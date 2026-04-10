package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.R
import com.dexter.app.domain.model.User
import com.dexter.app.domain.repository.UserRepository
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
    private val repository: UserRepository
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

    // ── Registration — now calls the API via repository ─────────

    /**
     * Registers the user by calling POST /auth/register via the repository.
     *
     * [email] and [password] come from navigation arguments (collected in WelcomeScreen).
     * [onRegisterSuccess] is called after a successful API call to trigger navigation.
     */
    fun register(email: String, password: String, onRegisterSuccess: () -> Unit) {
        if (_name.value.trim().isBlank()) {
            _state.value = RegisterState.Error(R.string.error_name_required)
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
                updatedAt = Date()
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
