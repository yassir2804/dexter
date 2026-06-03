package com.dexter.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Sealed class representing the various states of the forgot password flow.
 */
sealed class ForgotPasswordState {
    /** Form visible, no action taken yet */
    data object Idle : ForgotPasswordState()

    /** Sending reset email (spinner) */
    data object Loading : ForgotPasswordState()

    /** Email sent successfully — confirmation view */
    data class Sent(val email: String) : ForgotPasswordState()

    /** Validation or sending error */
    data class Error(val message: String) : ForgotPasswordState()
}

/**
 * ViewModel for the Forgot Password screen.
 *
 * No repository dependency — the email sending is mocked with a delay.
 */
class ForgotPasswordViewModel : ViewModel() {

    companion object {
        private const val SIMULATED_DELAY_MS = 1500L
    }

    private val _state = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val state = _state.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    fun onEmailChange(value: String) {
        _email.value = value
    }

    /**
     * Validates the email and simulates sending a reset email.
     */
    fun sendResetEmail() {
        val currentEmail = _email.value.trim()

        if (currentEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()) {
            _state.value = ForgotPasswordState.Error("Ingresa un correo válido")
            return
        }

        viewModelScope.launch {
            _state.value = ForgotPasswordState.Loading
            delay(SIMULATED_DELAY_MS)
            _state.value = ForgotPasswordState.Sent(email = currentEmail)
        }
    }

    /**
     * Resends the reset email (same simulated flow).
     */
    fun resendEmail() {
        viewModelScope.launch {
            _state.value = ForgotPasswordState.Loading
            delay(SIMULATED_DELAY_MS)
            _state.value = ForgotPasswordState.Sent(email = _email.value.trim())
        }
    }

    /**
     * Resets the state back to Idle (form view).
     */
    fun resetState() {
        _state.value = ForgotPasswordState.Idle
    }
}
