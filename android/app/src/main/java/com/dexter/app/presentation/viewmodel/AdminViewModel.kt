package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.R
import com.dexter.app.domain.model.User
import com.dexter.app.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminState {
    data object Loading : AdminState()
    data class Success(
        val users: List<User>,
        // Mensaje de éxito localizable: recurso + argumento (nombre del usuario).
        @StringRes val actionMessageRes: Int? = null,
        val actionMessageArg: String? = null,
        // Mensaje de error (ya viene específico desde el backend vía ApiErrorParser).
        val actionErrorMessage: String? = null
    ) : AdminState()
    data class Error(val message: String) : AdminState()
}

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    companion object { private const val TAG = "AdminViewModel" }

    private val _state = MutableStateFlow<AdminState>(AdminState.Loading)
    val state = _state.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _state.value = AdminState.Loading
            repository.getUsers()
                .onSuccess { users ->
                    _state.value = AdminState.Success(users = users)
                }
                .onFailure { e ->
                    Log.e(TAG, "Error loading users", e)
                    _state.value = AdminState.Error(e.message ?: "Error al cargar usuarios")
                }
        }
    }

    fun toggleUserStatus(userId: Long) {
        viewModelScope.launch {
            repository.toggleUserStatus(userId)
                .onSuccess { updatedUser ->
                    val current = _state.value as? AdminState.Success ?: return@launch
                    val updatedList = current.users.map {
                        if (it.id == userId) updatedUser else it
                    }
                    _state.value = current.copy(
                        users = updatedList,
                        actionMessageRes = if (updatedUser.enabled) {
                            R.string.admin_user_enabled
                        } else {
                            R.string.admin_user_disabled
                        },
                        actionMessageArg = updatedUser.firstName,
                        actionErrorMessage = null
                    )
                }
                .onFailure { e ->
                    Log.e(TAG, "Error toggling user status", e)
                    val current = _state.value as? AdminState.Success ?: return@launch
                    _state.value = current.copy(actionErrorMessage = e.message)
                }
        }
    }

    fun changeUserRole(userId: Long, roleName: String) {
        viewModelScope.launch {
            repository.changeUserRole(userId, roleName)
                .onSuccess { updatedUser ->
                    val current = _state.value as? AdminState.Success ?: return@launch
                    val updatedList = current.users.map {
                        if (it.id == userId) updatedUser else it
                    }
                    _state.value = current.copy(
                        users = updatedList,
                        actionMessageRes = if (roleName == "ROLE_ADMIN") {
                            R.string.admin_user_now_admin
                        } else {
                            R.string.admin_user_now_regular
                        },
                        actionMessageArg = updatedUser.firstName,
                        actionErrorMessage = null
                    )
                }
                .onFailure { e ->
                    Log.e(TAG, "Error changing role", e)
                    val current = _state.value as? AdminState.Success ?: return@launch
                    _state.value = current.copy(actionErrorMessage = e.message)
                }
        }
    }

    fun clearActionMessage() {
        val current = _state.value as? AdminState.Success ?: return
        _state.value = current.copy(
            actionMessageRes = null,
            actionMessageArg = null,
            actionErrorMessage = null
        )
    }
}
