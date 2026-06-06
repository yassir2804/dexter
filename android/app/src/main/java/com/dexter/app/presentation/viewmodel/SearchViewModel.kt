package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.SkillSummary
import com.dexter.app.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SearchState {
    data object Loading : SearchState()
    data class Success(val skills: List<SkillSummary>) : SearchState()
    data object Empty : SearchState()
    data class Error(val message: String) : SearchState()
}

/**
 * ViewModel for the Search screen.
 *
 * Loads categories and skills from the API on init, then
 * filters locally (no extra API calls for category / text filters).
 * Navigation to PeopleListScreen is handled by NavGraph callbacks.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel"
        private const val SEARCH_DEBOUNCE_MS = 300L
        private const val TOP_N = 5
    }

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Loading)
    val searchState = _searchState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private var popularSkills: List<SkillSummary> = emptyList()
    private var searchJob: Job? = null

    fun loadInitialData() {
        viewModelScope.launch {
            _searchState.value = SearchState.Loading
            repository.getPopularSkills()
                .onSuccess { skills ->
                    popularSkills = skills
                    _searchState.value = if (skills.isNotEmpty()) SearchState.Success(skills)
                    else SearchState.Empty
                }
                .onFailure { e ->
                    Log.e(TAG, "Error loading popular skills: ${e.message}")
                    _searchState.value = SearchState.Error(e.message ?: "Error desconocido")
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        searchJob?.cancel()

        if (newQuery.isBlank()) {
            _searchState.value = if (popularSkills.isNotEmpty()) SearchState.Success(popularSkills)
            else SearchState.Empty
            return
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            repository.searchSkills(newQuery)
                .onSuccess { skills ->
                    val top5 = skills.sortedByDescending { it.peopleCount }.take(TOP_N)
                    _searchState.value = if (top5.isNotEmpty()) SearchState.Success(top5)
                    else SearchState.Empty
                }
                .onFailure { e ->
                    Log.e(TAG, "Error searching skills: ${e.message}")
                    _searchState.value = SearchState.Error(e.message ?: "Error desconocido")
                }
        }
    }
}
