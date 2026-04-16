package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.InvitationStatus
import com.dexter.app.domain.model.SessionInvitation
import com.dexter.app.domain.repository.SessionInvitationRepository
import com.dexter.app.presentation.session.CurrentUserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed class SessionInviteState {
    data object Loading : SessionInviteState()
    data class Receive(val invitation: SessionInvitation, val responding: Boolean) : SessionInviteState()
    data class Send(val topic: String, val message: String, val sending: Boolean) : SessionInviteState()
    data object Done : SessionInviteState()
    data class Error(val message: String) : SessionInviteState()
}

/**
 * Bimodal ViewModel:
 * - invitationId > 0 → "receive" mode: loads an existing invitation and lets
 *   the user accept/reject.
 * - invitationId == 0 → "send" mode: lets the user compose a new invitation.
 */
@HiltViewModel
class SessionInviteViewModel @Inject constructor(
    private val repository: SessionInvitationRepository
) : ViewModel() {

    companion object { private const val TAG = "SessionInviteViewModel" }

    private val _state = MutableStateFlow<SessionInviteState>(SessionInviteState.Loading)
    val state = _state.asStateFlow()

    fun load(invitationId: Long) {
        if (invitationId <= 0L) {
            _state.value = SessionInviteState.Send(topic = "", message = "", sending = false)
            return
        }
        viewModelScope.launch {
            _state.value = SessionInviteState.Loading
            repository.getInvitationById(invitationId)
                .onSuccess { invite ->
                    _state.value = SessionInviteState.Receive(invite, responding = false)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading invitation", exception)
                    _state.value = SessionInviteState.Error(
                        exception.message ?: "Error desconocido"
                    )
                }
        }
    }

    fun onTopicChange(value: String) {
        val s = _state.value as? SessionInviteState.Send ?: return
        _state.value = s.copy(topic = value)
    }

    fun onMessageChange(value: String) {
        val s = _state.value as? SessionInviteState.Send ?: return
        _state.value = s.copy(message = value)
    }

    fun onAccept() = respond(accept = true)
    fun onReject() = respond(accept = false)

    private fun respond(accept: Boolean) {
        val current = _state.value as? SessionInviteState.Receive ?: return
        viewModelScope.launch {
            _state.value = current.copy(responding = true)
            repository.respondInvitation(current.invitation.id, accept)
                .onSuccess { _state.value = SessionInviteState.Done }
                .onFailure { exception ->
                    Log.e(TAG, "Error responding invitation", exception)
                    _state.value = current.copy(responding = false)
                }
        }
    }

    fun onSend(targetUserId: Long) {
        val current = _state.value as? SessionInviteState.Send ?: return
        if (current.topic.isBlank()) return
        viewModelScope.launch {
            _state.value = current.copy(sending = true)
            val invite = SessionInvitation(
                id = 0L,
                senderId = CurrentUserSession.userId ?: 1L,
                receiverId = targetUserId,
                matchId = 0L,
                status = InvitationStatus.PENDING,
                expiresAt = null,
                createdAt = Date()
            )
            repository.sendInvitation(invite)
                .onSuccess { _state.value = SessionInviteState.Done }
                .onFailure { exception ->
                    Log.e(TAG, "Error sending invitation", exception)
                    _state.value = current.copy(sending = false)
                }
        }
    }
}
