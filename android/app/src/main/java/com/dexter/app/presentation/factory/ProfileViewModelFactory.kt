package com.dexter.app.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dexter.app.domain.repository.ReviewRepository
import com.dexter.app.domain.repository.SkillRepository
import com.dexter.app.domain.repository.UserRepository
import com.dexter.app.presentation.viewmodel.ProfileViewModel

/**
 * Factory that provides a [ProfileViewModel] with its repository dependencies.
 *
 * Required because we use manual constructor injection (no Hilt).
 * Mirrors TaskApp's ViewModelFactory pattern.
 */
class ProfileViewModelFactory(
    private val userRepository: UserRepository,
    private val skillRepository: SkillRepository,
    private val reviewRepository: ReviewRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(userRepository, skillRepository, reviewRepository) as T
    }
}
