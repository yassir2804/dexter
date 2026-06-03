package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.MatchSummary
import com.dexter.app.domain.repository.MatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeState {
    data object Loading : HomeState()
    data class Success(
        val pendingMatches: List<MatchSummary>,
        val acceptedMatches: List<MatchSummary>,
        val suggestions: List<MatchSummary> = emptyList(),
    ) : HomeState()
    data object Empty : HomeState()
    data class Error(val message: String) : HomeState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MatchRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeState>(HomeState.Empty)
    val homeState = _homeState.asStateFlow()

    private val _connectingUserId = MutableStateFlow<Long?>(null)
    val connectingUserId = _connectingUserId.asStateFlow()

    fun loadMatches(userId: Long) {
        viewModelScope.launch {
            _homeState.value = HomeState.Loading

            val matchesDeferred = async { repository.getMatchSummaries(userId) }
            val suggestionsDeferred = async { repository.getSuggestions(userId) }

            val matchesResult = matchesDeferred.await()
            val suggestions = suggestionsDeferred.await().getOrElse { emptyList() }

            matchesResult
                .onSuccess { matches ->
                    Log.d("DEXTER_HOME", "matches recibidos: ${matches.size}")
                    matches.forEachIndexed { i, m ->
                        Log.d("DEXTER_HOME", "  match[$i] id=${m.matchId} status=${m.status} " +
                            "offeredNull=${m.skillsOffered == null} soughtNull=${m.skillsSought == null} " +
                            "availNull=${m.availability == null} name='${m.name}'")
                    }
                    Log.d("DEXTER_HOME", "suggestions recibidas: ${suggestions.size}")
                    suggestions.forEachIndexed { i, s ->
                        Log.d("DEXTER_HOME", "  sug[$i] id=${s.otherUserId} " +
                            "offeredNull=${s.skillsOffered == null} soughtNull=${s.skillsSought == null} " +
                            "availNull=${s.availability == null} name='${s.name}'")
                    }

                    val pending = matches.filter { it.status == "PENDING" && it.isReceiver }
                    val accepted = matches.filter { it.status == "ACCEPTED" }
                    if (pending.isEmpty() && accepted.isEmpty() && suggestions.isEmpty()) {
                        _homeState.value = HomeState.Empty
                    } else {
                        _homeState.value = HomeState.Success(pending, accepted, suggestions)
                    }
                }
                .onFailure { exception ->
                    Log.e("DEXTER_HOME", "Error loading matches: ${exception.message}", exception)
                    _homeState.value = HomeState.Error(exception.message ?: "Error desconocido")
                }
        }
    }

    fun connectSuggestion(initiatorId: Long, receiverId: Long) {
        if (_connectingUserId.value != null) return
        viewModelScope.launch {
            _connectingUserId.value = receiverId
            try {
                repository.initiateMatch(initiatorId, receiverId)
                    .onSuccess { loadMatches(initiatorId) }
                    .onFailure { Log.e("HomeViewModel", "Error connecting: ${it.message}") }
            } finally {
                _connectingUserId.value = null
            }
        }
    }
}
