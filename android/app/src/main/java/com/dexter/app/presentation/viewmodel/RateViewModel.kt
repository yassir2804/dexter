package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.Review
import com.dexter.app.domain.repository.MatchRepository
import com.dexter.app.domain.repository.ReviewRepository
import com.dexter.app.domain.repository.ReviewTagRepository
import com.dexter.app.domain.repository.SessionRepository
import com.dexter.app.presentation.session.CurrentUserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed class RateState {
    data object Loading : RateState()
    data class Editing(
        val revieweeName: String = "",
        val revieweeInitials: String = "",
        val revieweeAvatarUrl: String? = null,
        val sessionNumber: Int = 0,
        val tagCatalog: List<String>,
        val rating: Int,
        val selectedTags: Set<String>,
        val comment: String,
        val submitting: Boolean,
        val showRatingError: Boolean = false
    ) : RateState()
    data object Submitted : RateState()
    data class Error(val message: String) : RateState()
}

@HiltViewModel
class RateViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val reviewTagRepository: ReviewTagRepository,
    private val sessionRepository: SessionRepository,
    private val matchRepository: MatchRepository
) : ViewModel() {

    companion object { private const val TAG = "RateViewModel" }

    private val _state = MutableStateFlow<RateState>(RateState.Loading)
    val state = _state.asStateFlow()

    private var sessionId: Long = 0L
    private var revieweeId: Long = 0L
    private var revieweeName: String = ""
    private var revieweeInitials: String = ""
    private var revieweeAvatarUrl: String? = null
    private var sessionNumber: Int = 0

    fun loadTags(sessionId: Long) {
        this.sessionId = sessionId
        viewModelScope.launch {
            _state.value = RateState.Loading

            resolveRevieweeFromSession(sessionId)

            reviewTagRepository.getCatalog()
                .onSuccess { catalog ->
                    _state.value = RateState.Editing(
                        revieweeName = revieweeName,
                        revieweeInitials = revieweeInitials,
                        revieweeAvatarUrl = revieweeAvatarUrl,
                        sessionNumber = sessionNumber,
                        tagCatalog = catalog,
                        rating = 0,
                        selectedTags = emptySet(),
                        comment = "",
                        submitting = false,
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading tag catalog", exception)
                    _state.value = RateState.Error(
                        exception.message ?: "Error desconocido"
                    )
                }
        }
    }

    private suspend fun resolveRevieweeFromSession(sessionId: Long) {
        val currentUserId = CurrentUserSession.userId ?: return
        sessionRepository.getSessionById(sessionId)
            .onSuccess { session ->
                sessionNumber = session.sessionNumber
                matchRepository.getMatchDetail(session.matchId, currentUserId)
                    .onSuccess { detail ->
                        revieweeId = detail.otherUserId
                        revieweeName = detail.name
                        revieweeInitials = detail.initials
                        revieweeAvatarUrl = detail.avatarUrl
                    }
                    .onFailure { Log.e(TAG, "Could not load match ${session.matchId}", it) }
            }
            .onFailure { Log.e(TAG, "Could not load session $sessionId", it) }
    }

    private fun currentEditing(): RateState.Editing? =
        _state.value as? RateState.Editing

    fun onRatingChange(rating: Int) {
        currentEditing()?.let { _state.value = it.copy(rating = rating, showRatingError = false) }
    }

    fun onTagToggle(tag: String) {
        currentEditing()?.let { editing ->
            val newSet = if (editing.selectedTags.contains(tag)) {
                editing.selectedTags - tag
            } else {
                editing.selectedTags + tag
            }
            _state.value = editing.copy(selectedTags = newSet)
        }
    }

    fun onCommentChange(value: String) {
        currentEditing()?.let { _state.value = it.copy(comment = value) }
    }

    fun onSubmit() {
        val editing = currentEditing() ?: return
        if (editing.rating <= 0) {
            _state.value = editing.copy(showRatingError = true)
            return
        }
        viewModelScope.launch {
            _state.value = editing.copy(submitting = true)
            val review = Review(
                id = 0L,
                reviewerId = CurrentUserSession.userId ?: 0L,
                revieweeId = revieweeId,
                sessionId = sessionId,
                rating = editing.rating,
                comment = editing.comment.takeIf { it.isNotBlank() },
                createdAt = Date(),
                tags = editing.selectedTags.toList(),
            )
            reviewRepository.createReview(review)
                .onSuccess { _state.value = RateState.Submitted }
                .onFailure { exception ->
                    Log.e(TAG, "Error submitting review", exception)
                    _state.value = RateState.Error(
                        exception.message ?: "No se pudo enviar la resena"
                    )
                }
        }
    }
}
