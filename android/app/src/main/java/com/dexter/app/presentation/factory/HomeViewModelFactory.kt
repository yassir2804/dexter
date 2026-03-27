package com.dexter.app.presentation.factory



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dexter.app.domain.repository.MatchRepository
import com.dexter.app.presentation.viewmodel.HomeViewModel

class HomeViewModelFactory(
    private val repository: MatchRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}
