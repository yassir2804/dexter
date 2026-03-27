package com.dexter.app.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dexter.app.domain.repository.UserRepository
import com.dexter.app.presentation.viewmodel.ProfileViewModel

/**
 * Factory that provides a [ProfileViewModel] with its repository dependency.
 *
 * Required because we use manual constructor injection (no Hilt).
 * Mirrors TaskApp's ViewModelFactory pattern.
 */
class ProfileViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository) as T
    }
}
