package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.Notification
import com.dexter.app.domain.model.NotificationType
import com.dexter.app.domain.repository.NotificationRepository
import com.dexter.app.presentation.session.NotificationPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

    private val _allNotifications = MutableStateFlow<List<Notification>?>(null) // null = aún cargando
    private val _loadError = MutableStateFlow<String?>(null)

    private val _state = MutableStateFlow<NotificationsState>(NotificationsState.Loading)
    val state = _state.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount = _unreadCount.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                _allNotifications,
                _loadError,
                NotificationPreference.showMatches,
                NotificationPreference.showReminders,
                NotificationPreference.showRatings
            ) { all, error, showMatches, showReminders, showRatings ->
                // null = todavía cargando, no emitir nada
                if (all == null && error == null) return@combine NotificationsState.Loading
                if (error != null) return@combine NotificationsState.Error(error)
                val filtered = all!!.filter { notif ->
                    when (notif.notificationType) {
                        NotificationType.NEW_MATCH -> showMatches
                        NotificationType.SESSION_REMINDER -> showReminders
                        NotificationType.NEW_REVIEW -> showRatings
                        NotificationType.AI_SUGGESTION -> true
                    }
                }
                if (filtered.isEmpty()) return@combine NotificationsState.Empty
                val (unread, read) = filtered.partition { !it.isRead }
                _unreadCount.value = unread.size
                NotificationsState.Success(
                    unread = unread.sortedByDescending { it.createdAt },
                    read = read.sortedByDescending { it.createdAt }
                )
            }.collect { _state.value = it }
        }
    }

    fun loadNotifications(userId: Long) {
        viewModelScope.launch {
            _allNotifications.value = null  // null dispara Loading en el combine
            _loadError.value = null
            repository.getNotificationsForUser(userId)
                .onSuccess { notifs ->
                    _allNotifications.value = notifs
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading notifications", exception)
                    _loadError.value = exception.message ?: "Error desconocido"
                }
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
                .onSuccess {
                    _allNotifications.value = _allNotifications.value.map {
                        if (it.id == notificationId) it.copy(isRead = true) else it
                    }
                }
                .onFailure { Log.e(TAG, "Error marking notification as read", it) }
        }
    }

    fun onMarkAllRead(userId: Long) {
        viewModelScope.launch {
            repository.markAllAsRead(userId)
                .onSuccess {
                    _allNotifications.value = _allNotifications.value.map { it.copy(isRead = true) }
                }
                .onFailure { Log.e(TAG, "Error marking all as read", it) }
        }
    }
}
