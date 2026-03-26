package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.User
import com.dexter.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {

    data object Loading : LoginState()

    /** Contains the successfully retrieved user */
    data class Success(val user: User) : LoginState()

    /** Indicates no user is available */
    data object Empty : LoginState()

    /** Contains an error message if the user operation fails */
    data class Error(val message: String) : LoginState()
}

/**
 * ViewModel for the Login screen.
 *
 * Follows TaskApp's ViewModel pattern: exposes read-only StateFlows,
 * mutates state through private MutableStateFlows, and launches
 * coroutines via viewModelScope.
 */
class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Empty)
    val loginState = _loginState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible = _passwordVisible.asStateFlow()

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun login() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val user = repository.login(_email.value, _password.value)
                if (user != null) {
                    _loginState.value = LoginState.Success(user)
                } else {
                    _loginState.value = LoginState.Error("Credenciales inválidas")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login failed", e)
                _loginState.value = LoginState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Empty
    }
}

