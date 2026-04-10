package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.PersonResult
import com.dexter.app.domain.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the People List screen.
 *
 * Follows the same Loading / Success / Empty / Error pattern
 * used in [HomeState] (professor's approved architecture).
 */
sealed class PeopleListState {
    data object Loading : PeopleListState()
    data class Success(
        val skillName: String,
        val people: List<PersonResult>
    ) : PeopleListState()
    data object Empty : PeopleListState()
    data class Error(val message: String) : PeopleListState()
}

/**
 * ViewModel for the People List screen.
 *
 * Loads people who offer a specific skill via [SearchRepository].
 * Uses [SearchRepository.getPeopleForSkill] which calls
 * GET /search/people?skill={skillName} through Retrofit.
 */
@HiltViewModel
class PeopleListViewModel @Inject constructor(
    private val repository: SearchRepository
) : ViewModel() {

    companion object {
        private const val TAG = "PeopleListViewModel"
    }

    private val _peopleListState = MutableStateFlow<PeopleListState>(PeopleListState.Loading)
    val peopleListState = _peopleListState.asStateFlow()

    fun loadPeople(skillName: String) {
        viewModelScope.launch {
            _peopleListState.value = PeopleListState.Loading

            repository.getPeopleForSkill(skillName)
                .onSuccess { people ->
                    if (people.isNotEmpty()) {
                        _peopleListState.value = PeopleListState.Success(skillName, people)
                    } else {
                        _peopleListState.value = PeopleListState.Empty
                    }
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading people for skill '$skillName': ${exception.message}")
                    _peopleListState.value =
                        PeopleListState.Error(exception.message ?: "Error desconocido")
                }
        }
    }
}
