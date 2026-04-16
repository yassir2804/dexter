package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.MatchDetail
import com.dexter.app.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MatchDetailState {
    data object Loading : MatchDetailState()
    data class Success(val match: MatchDetail) : MatchDetailState()
    data class Error(val message: String) : MatchDetailState()
}

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    private val repository: MatchRepository
) : ViewModel() {

    companion object { private const val TAG = "MatchDetailViewModel" }

    private val _state = MutableStateFlow<MatchDetailState>(MatchDetailState.Loading)
    val state = _state.asStateFlow()

    fun loadMatch(matchId: Long) {
        viewModelScope.launch {
            _state.value = MatchDetailState.Loading
            repository.getMatchDetail(matchId)
                .onSuccess { detail ->
                    _state.value = MatchDetailState.Success(detail)
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading match detail: ${exception.message}")
                    _state.value = MatchDetailState.Error(
                        exception.message ?: "Error desconocido"
                    )
                }
        }
    }
}
