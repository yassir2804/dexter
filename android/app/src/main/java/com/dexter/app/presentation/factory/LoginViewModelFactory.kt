package com.dexter.app.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dexter.app.domain.repository.UserRepository
import com.dexter.app.presentation.viewmodel.LoginViewModel

/**
 * Factory that provides a [LoginViewModel] with its [UserRepository] dependency.
 *
 * Required because we use manual constructor injection (no Hilt).
 * Mirrors TaskApp's TaskViewModelFactory pattern.
 */
class LoginViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repository) as T
    }
}
