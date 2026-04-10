package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.SkillSummary
import com.dexter.app.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Search screen.
 *
 * Follows the same Loading / Success / Empty / Error pattern
 * used in [HomeState] (professor's approved architecture).
 */
sealed class SearchState {
    data object Loading : SearchState()
    data class Success(
        val categories: List<String>,
        val skills: List<SkillSummary>,
        val selectedCategory: String
    ) : SearchState()
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
        private const val DEFAULT_CATEGORY = "Todas"
    }

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Loading)
    val searchState = _searchState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private var allCategories: List<String> = emptyList()
    private var allSkills: List<SkillSummary> = emptyList()

    fun loadInitialData() {
        viewModelScope.launch {
            _searchState.value = SearchState.Loading

            repository.getCategories()
                .onSuccess { categories ->
                    allCategories = categories
                    repository.getPopularSkills()
                        .onSuccess { skills ->
                            allSkills = skills
                            if (skills.isNotEmpty()) {
                                _searchState.value = SearchState.Success(
                                    categories = categories,
                                    skills = skills,
                                    selectedCategory = categories.firstOrNull() ?: DEFAULT_CATEGORY
                                )
                            } else {
                                _searchState.value = SearchState.Empty
                            }
                        }
                        .onFailure { e ->
                            Log.e(TAG, "Error loading skills: ${e.message}")
                            _searchState.value = SearchState.Error(e.message ?: "Error desconocido")
                        }
                }
                .onFailure { e ->
                    Log.e(TAG, "Error loading categories: ${e.message}")
                    _searchState.value = SearchState.Error(e.message ?: "Error desconocido")
                }
        }
    }

    /**
     * Filters skills locally by text query.
     * No API call needed — filters from the cached [allSkills] list.
     *
     * Always keeps [SearchState.Success] so categories remain visible.
     * An empty [skills] list shows "Sin resultados" inline (not a separate Empty state).
     */
    fun onQueryChange(newQuery: String) {
        _query.value = newQuery

        if (newQuery.isBlank()) {
            // Reset to full list with default category
            _searchState.value = SearchState.Success(
                categories = allCategories,
                skills = allSkills,
                selectedCategory = allCategories.firstOrNull() ?: DEFAULT_CATEGORY
            )
            return
        }

        val filtered = allSkills.filter {
            it.name.contains(newQuery, ignoreCase = true)
        }

        _searchState.value = SearchState.Success(
            categories = allCategories,
            skills = filtered,
            selectedCategory = allCategories.firstOrNull() ?: DEFAULT_CATEGORY
        )
    }

    /**
     * Filters skills locally by category.
     * No API call needed — filters from the cached [allSkills] list.
     *
     * Always keeps [SearchState.Success] so categories remain visible.
     * An empty [skills] list shows "Sin resultados" inline (not a separate Empty state).
     */
    fun onCategorySelect(category: String) {
        val filtered = if (category == DEFAULT_CATEGORY) {
            allSkills
        } else {
            allSkills.filter { it.category == category }
        }

        _searchState.value = SearchState.Success(
            categories = allCategories,
            skills = filtered,
            selectedCategory = category
        )
    }
}
