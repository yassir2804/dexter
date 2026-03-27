package com.dexter.app.presentation.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.Match
import com.dexter.app.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

sealed class HomeState {
    data object Loading : HomeState()
    data class Success(val matches: List<Match>) : HomeState()
    data object Empty : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel(
    private val repository: MatchRepository
) : ViewModel() {

    companion object { private const val TAG = "HomeViewModel" }

    private val _state = MutableStateFlow<HomeState>(HomeState.Empty)
    val state: StateFlow<HomeState> = _state

    fun loadMatchesForUser(userId: Long) {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            try {
                val result = repository.getMatchesForUser(userId)
                result
                    .onSuccess { list ->
                        if (list.isEmpty()) _state.value = HomeState.Empty
                        else _state.value = HomeState.Success(list)
                    }
                    .onFailure { ex ->
                        Log.e(TAG, "Error loading matches: ${ex.message}")
                        val message = when (ex) {
                            is DomainError -> ex.message ?: "Unknown domain error"
                            else -> ex.message ?: "Unknown error"
                        }
                        _state.value = HomeState.Error(message)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Unhandled error loading matches", e)
                _state.value = HomeState.Error(e.message ?: "Unhandled error")
            }
        }
    }
}
