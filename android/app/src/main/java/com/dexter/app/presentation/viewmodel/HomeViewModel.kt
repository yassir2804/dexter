package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.MatchSummary
import com.dexter.app.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    data object Loading : HomeState()
    data class Success(val matches: List<MatchSummary>) : HomeState()
    data object Empty : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(
    private val repository: MatchRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Empty)
    val homeState = _homeState.asStateFlow()

    fun loadMatches(userId: Long) {
        viewModelScope.launch {
            _homeState.value = HomeState.Loading

            repository.getMatchSummaries(userId)
                .onSuccess { matches ->
                    if (matches.isNotEmpty()) {
                        _homeState.value = HomeState.Success(matches)
                    } else {
                        _homeState.value = HomeState.Empty
                    }
                }
                .onFailure { exception ->
                    Log.e("HomeViewModel", "Error loading matches: ${exception.message}")
                    _homeState.value = HomeState.Error(exception.message ?: "Error desconocido")
                }
        }
    }
}
