package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.PersonResult
import com.dexter.app.domain.model.SkillSummary
import com.dexter.app.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Search screen.
 * Has two content states: [SkillList] for the main view
 * and [PeopleList] for the drill-down view.
 */
sealed class SearchState {
    data object Loading : SearchState()
    data class SkillList(
        val categories: List<String>,
        val skills: List<SkillSummary>,
        val selectedCategory: String
    ) : SearchState()
    data class PeopleList(
        val skillName: String,
        val people: List<PersonResult>
    ) : SearchState()
    data object Empty : SearchState()
    data class Error(val message: String) : SearchState()
}

/**
 * ViewModel for the Search screen.
 * Manages search query, category filtering, and skill drill-down.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

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
                            _searchState.value = SearchState.SkillList(
                                categories = categories,
                                skills = skills,
                                selectedCategory = categories.firstOrNull() ?: ""
                            )
                        }
                        .onFailure { e ->
                            Log.e("SearchViewModel", "Error loading skills", e)
                            _searchState.value = SearchState.Error(e.message ?: "Error loading skills")
                        }
                }
                .onFailure { e ->
                    Log.e("SearchViewModel", "Error loading categories", e)
                    _searchState.value = SearchState.Error(e.message ?: "Error loading categories")
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        viewModelScope.launch {
            if (newQuery.isBlank()) {
                _searchState.value = SearchState.SkillList(
                    categories = allCategories,
                    skills = allSkills,
                    selectedCategory = allCategories.firstOrNull() ?: ""
                )
            } else {
                repository.searchSkills(newQuery)
                    .onSuccess { filtered ->
                        if (filtered.isEmpty()) {
                            _searchState.value = SearchState.Empty
                        } else {
                            _searchState.value = SearchState.SkillList(
                                categories = allCategories,
                                skills = filtered,
                                selectedCategory = allCategories.firstOrNull() ?: ""
                            )
                        }
                    }
                    .onFailure { e ->
                        _searchState.value = SearchState.Error(e.message ?: "Error searching")
                    }
            }
        }
    }

    fun onCategorySelect(category: String) {
        viewModelScope.launch {
            repository.filterSkillsByCategory(category)
                .onSuccess { filtered ->
                    _searchState.value = SearchState.SkillList(
                        categories = allCategories,
                        skills = filtered,
                        selectedCategory = category
                    )
                }
                .onFailure { e ->
                    _searchState.value = SearchState.Error(e.message ?: "Error filtering")
                }
        }
    }

    fun onSkillClick(skillName: String) {
        viewModelScope.launch {
            _searchState.value = SearchState.Loading
            repository.getPeopleForSkill(skillName)
                .onSuccess { people ->
                    _searchState.value = SearchState.PeopleList(
                        skillName = skillName,
                        people = people
                    )
                }
                .onFailure { e ->
                    _searchState.value = SearchState.Error(e.message ?: "Error loading people")
                }
        }
    }

    fun onBackToSkills() {
        _query.value = ""
        _searchState.value = SearchState.SkillList(
            categories = allCategories,
            skills = allSkills,
            selectedCategory = allCategories.firstOrNull() ?: ""
        )
    }
}
