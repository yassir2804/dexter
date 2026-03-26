package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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



