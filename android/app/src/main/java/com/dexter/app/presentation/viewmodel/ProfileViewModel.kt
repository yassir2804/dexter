package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.error.DomainError
import com.dexter.app.domain.model.Review
import com.dexter.app.domain.model.Skill
import com.dexter.app.domain.model.SkillRole
import com.dexter.app.domain.model.User
import com.dexter.app.domain.repository.ReviewRepository
import com.dexter.app.domain.repository.SkillRepository
import com.dexter.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Success(
        val user: User,
        val skillsOffered: List<Skill>,
        val skillsSought: List<Skill>,
        val reviews: List<ReviewWithReviewer>
    ) : ProfileState()
    data object Empty : ProfileState()
    data class Error(val message: String) : ProfileState()
}

data class ReviewWithReviewer(
    val review: Review,
    val reviewerName: String,
    val reviewerAvatarUrl: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val skillRepository: SkillRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    companion object { private const val TAG = "ProfileViewModel" }

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Empty)
    val profileState: StateFlow<ProfileState> = _profileState

    fun loadProfile(userId: Long) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                userRepository.getUserById(userId)
                    .onSuccess { user ->
                        val skills = skillRepository.getSkillsForUser(userId).getOrDefault(emptyList())
                        val offered = skills.filter { it.skillRole == SkillRole.OFFERED }
                        val sought = skills.filter { it.skillRole == SkillRole.SOUGHT }

                        val reviews = reviewRepository.getReviewsForUser(userId).getOrDefault(emptyList())
                        val reviewsWithNames = reviews.map { review ->
                            val reviewer = userRepository.getUserById(review.reviewerId).getOrNull()
                            ReviewWithReviewer(
                                review = review,
                                reviewerName = reviewer?.let { "${it.firstName} ${it.lastName}" } ?: "Usuario",
                                reviewerAvatarUrl = reviewer?.avatarUrl
                            )
                        }

                        _profileState.value = ProfileState.Success(user, offered, sought, reviewsWithNames)
                    }
                    .onFailure { ex ->
                        Log.e(TAG, "Error loading profile: ${ex.message}")
                        val message = when (ex) {
                            is DomainError -> ex.message ?: "Unknown domain error"
                            else -> ex.message ?: "Unknown error"
                        }
                        _profileState.value = ProfileState.Error(message)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Unhandled error loading profile", e)
                _profileState.value = ProfileState.Error(e.message ?: "Unhandled error")
            }
        }
    }
}
