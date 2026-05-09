package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.Review
import com.dexter.app.domain.model.ReviewTag
import com.dexter.app.domain.repository.ReviewRepository
import com.dexter.app.domain.repository.ReviewTagRepository
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
        val tags: List<ReviewTag>,
        val rating: Int,
        val selectedTagIds: Set<Long>,
        val comment: String,
        val submitting: Boolean
    ) : RateState()
    data object Submitted : RateState()
    data class Error(val message: String) : RateState()
}

@HiltViewModel
class RateViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val reviewTagRepository: ReviewTagRepository
) : ViewModel() {

    companion object { private const val TAG = "RateViewModel" }

    private val _state = MutableStateFlow<RateState>(RateState.Loading)
    val state = _state.asStateFlow()

    private var sessionId: Long = 0L

    fun loadTags(sessionId: Long) {
        this.sessionId = sessionId
        viewModelScope.launch {
            _state.value = RateState.Loading
            reviewTagRepository.getAvailableTags()
                .onSuccess { tags ->
                    _state.value = RateState.Editing(
                        tags = tags,
                        rating = 0,
                        selectedTagIds = emptySet(),
                        comment = "",
                        submitting = false
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading tags", exception)
                    _state.value = RateState.Error(
                        exception.message ?: "Error desconocido"
                    )
                }
        }
    }

    private fun currentEditing(): RateState.Editing? =
        _state.value as? RateState.Editing

    fun onRatingChange(rating: Int) {
        currentEditing()?.let { _state.value = it.copy(rating = rating) }
    }

    fun onTagToggle(tagId: Long) {
        currentEditing()?.let { editing ->
            val newSet = if (editing.selectedTagIds.contains(tagId)) {
                editing.selectedTagIds - tagId
            } else {
                editing.selectedTagIds + tagId
            }
            _state.value = editing.copy(selectedTagIds = newSet)
        }
    }

    fun onCommentChange(value: String) {
        currentEditing()?.let { _state.value = it.copy(comment = value) }
    }

    fun onSubmit() {
        val editing = currentEditing() ?: return
        if (editing.rating <= 0) return
        viewModelScope.launch {
            _state.value = editing.copy(submitting = true)
            val review = Review(
                id = 0L,
                reviewerId = CurrentUserSession.userId ?: 1L,
                revieweeId = 0L,
                sessionId = sessionId,
                rating = editing.rating,
                comment = editing.comment.takeIf { it.isNotBlank() },
                createdAt = Date()
            )
            reviewRepository.createReview(review)
                .onSuccess { _state.value = RateState.Submitted }
                .onFailure { exception ->
                    Log.e(TAG, "Error submitting review", exception)
                    _state.value = RateState.Error(
                        exception.message ?: "No se pudo enviar la reseña"
                    )
                }
        }
    }
}
