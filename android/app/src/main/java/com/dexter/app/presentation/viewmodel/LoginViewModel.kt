package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.User
import com.dexter.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Sealed class representing the various states of a login operation.
 */
sealed class LoginState {
    /** Indicates an ongoing login operation */
    data object Loading : LoginState()

    /** Contains the successfully authenticated user */
    data class Success(val user: User) : LoginState()

    /** Indicates no login action has been taken yet */
    data object Empty : LoginState()

    /** Contains an error message if the login operation fails */
    data class Error(val message: String) : LoginState()
}

/**
 * ViewModel responsible for managing login-related UI state and business logic.
 *
 * @property repository Repository interface for user operations
 */
class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Empty)
    val loginState: StateFlow<LoginState> = _loginState

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    /**
     * Attempts to authenticate the user with the current email and password.
     * Updates [loginState] throughout the operation lifecycle.
     */
    fun login() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            repository.login(_email.value, _password.value)
                .onSuccess { user ->
                    _loginState.value = LoginState.Success(user)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Login failed: ${exception.message}")
                    _loginState.value = LoginState.Error(
                        exception.message ?: "Unknown error"
                    )
                }
        }
    }

    /**
     * Resets the login state back to [LoginState.Empty].
     * Should be called when navigating back to the login screen.
     */
    fun resetState() {
        _loginState.value = LoginState.Empty
    }
}