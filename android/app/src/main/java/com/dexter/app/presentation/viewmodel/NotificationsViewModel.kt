package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.Notification
import com.dexter.app.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NotificationsState {
    data object Loading : NotificationsState()
    data class Success(
        val unread: List<Notification>,
        val read: List<Notification>
    ) : NotificationsState()
    data object Empty : NotificationsState()
    data class Error(val message: String) : NotificationsState()
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    companion object { private const val TAG = "NotificationsViewModel" }

    private val _state = MutableStateFlow<NotificationsState>(NotificationsState.Loading)
    val state = _state.asStateFlow()

    fun loadNotifications(userId: Long) {
        viewModelScope.launch {
            _state.value = NotificationsState.Loading
            repository.getNotificationsForUser(userId)
                .onSuccess { notifs ->
                    if (notifs.isEmpty()) {
                        _state.value = NotificationsState.Empty
                    } else {
                        val (unread, read) = notifs.partition { !it.isRead }
                        _state.value = NotificationsState.Success(
                            unread = unread.sortedByDescending { it.createdAt },
                            read = read.sortedByDescending { it.createdAt }
                        )
                    }
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading notifications", exception)
                    _state.value = NotificationsState.Error(
                        exception.message ?: "Error desconocido"
                    )
                }
        }
    }

    fun onMarkAllRead(userId: Long) {
        viewModelScope.launch {
            repository.markAllAsRead(userId)
                .onSuccess {
                    val current = _state.value as? NotificationsState.Success ?: return@onSuccess
                    val merged = (current.unread + current.read)
                        .map { it.copy(isRead = true) }
                        .sortedByDescending { it.createdAt }
                    _state.value = NotificationsState.Success(
                        unread = emptyList(),
                        read = merged
                    )
                }
                .onFailure { Log.e(TAG, "Error marking all as read", it) }
        }
    }
}
