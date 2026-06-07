package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        val actionMessage: String? = null
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
                    val status = if (updatedUser.enabled) "habilitado" else "deshabilitado"
                    _state.value = current.copy(
                        users = updatedList,
                        actionMessage = "Usuario ${updatedUser.firstName} $status"
                    )
                }
                .onFailure { e ->
                    Log.e(TAG, "Error toggling user status", e)
                    val current = _state.value as? AdminState.Success ?: return@launch
                    _state.value = current.copy(actionMessage = e.message ?: "Error al cambiar estado")
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
                    val roleLabel = if (roleName == "ROLE_ADMIN") "Admin" else "Usuario"
                    _state.value = current.copy(
                        users = updatedList,
                        actionMessage = "${updatedUser.firstName} ahora es $roleLabel"
                    )
                }
                .onFailure { e ->
                    Log.e(TAG, "Error changing role", e)
                    val current = _state.value as? AdminState.Success ?: return@launch
                    _state.value = current.copy(actionMessage = e.message ?: "Error al cambiar rol")
                }
        }
    }

    fun clearActionMessage() {
        val current = _state.value as? AdminState.Success ?: return
        _state.value = current.copy(actionMessage = null)
    }
}
