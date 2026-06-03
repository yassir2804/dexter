package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.MatchDetail
import com.dexter.app.domain.model.MatchStatus
import com.dexter.app.domain.model.Session
import com.dexter.app.domain.model.SessionStatus
import com.dexter.app.domain.repository.MatchRepository
import com.dexter.app.domain.repository.SessionRepository
import com.dexter.app.presentation.session.CurrentUserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PlanState {
    data object Loading : PlanState()
    data class Success(
        val match: MatchDetail,
        val sessions: List<Session>,
        val progress: Float,
        val nextSessionId: Long?
    ) : PlanState()
    data class Error(val message: String) : PlanState()
}

/**
 * Composes match detail + sessions for the PlanScreen.
 *
 * The progress bar shows ratio of COMPLETED sessions over the total
 * (expected 5 per roadmap). The "next" session is the first non-completed
 * one in order.
 */
@HiltViewModel
class PlanViewModel @Inject constructor(
    private val matchRepository: MatchRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    companion object { private const val TAG = "PlanViewModel" }

    private val _state = MutableStateFlow<PlanState>(PlanState.Loading)
    val state = _state.asStateFlow()

    private var currentMatchId: Long = 0L

    fun completeSession(sessionId: Long) {
        viewModelScope.launch {
            sessionRepository.completeSession(sessionId, "").onSuccess {
                loadPlan(currentMatchId)
            }.onFailure { e ->
                Log.e(TAG, "Error completing session $sessionId", e)
            }
        }
    }

    fun loadPlan(matchId: Long) {
        viewModelScope.launch {
            _state.value = PlanState.Loading
            currentMatchId = matchId

            val matchResult = matchRepository.getMatchDetail(matchId, CurrentUserSession.userId ?: 0L)
            val sessionsResult = sessionRepository.getSessionsByMatch(matchId)

            matchResult.onSuccess { detail ->
                var sessionsToShow = sessionsResult.getOrElse { emptyList() }

                if (sessionsToShow.isEmpty() && detail.status == MatchStatus.ACCEPTED) {
                    matchRepository.ensureSessions(matchId)
                    sessionsToShow = sessionRepository.getSessionsByMatch(matchId).getOrElse { emptyList() }
                }

                if (sessionsResult.isFailure && sessionsToShow.isEmpty()) {
                    _state.value = PlanState.Error(
                        sessionsResult.exceptionOrNull()?.message ?: "Error desconocido"
                    )
                } else {
                    val sorted = sessionsToShow.sortedBy { it.sessionNumber }
                    val completedCount = sorted.count { it.status == SessionStatus.COMPLETED }
                    val progress = if (sorted.isEmpty()) 0f
                    else completedCount.toFloat() / sorted.size
                    val next = sorted.firstOrNull { it.status != SessionStatus.COMPLETED }
                    _state.value = PlanState.Success(
                        match = detail,
                        sessions = sorted,
                        progress = progress,
                        nextSessionId = next?.id
                    )
                }
            }.onFailure { exception ->
                Log.e(TAG, "Error loading match", exception)
                _state.value = PlanState.Error(
                    exception.message ?: "Error desconocido"
                )
            }
        }
    }
}
