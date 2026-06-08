package com.dexter.backend.service

import com.dexter.backend.dto.ProfileReviewItemDetails
import com.dexter.backend.dto.ProfileSummaryResult
import com.dexter.backend.entity.Skill
import com.dexter.backend.entity.SkillLevel
import com.dexter.backend.entity.SkillRole
import com.dexter.backend.repository.ReviewRepository
import com.dexter.backend.repository.SkillRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class AbstractProfileService(
    @Autowired val userRepository: UserRepository,
    @Autowired val skillRepository: SkillRepository,
    @Autowired val reviewRepository: ReviewRepository,
) : ProfileService {

    override fun findByUserId(userId: Long): ProfileSummaryResult {
        val user = userRepository.findById(userId).orElseThrow {
            NoSuchElementException("User with id $userId not found")
        }

        val skills = skillRepository.findByUserId(userId)
        val offered = skills.filter { it.skillRole == SkillRole.OFFERED }.map { it.name }
        val sought = skills.filter { it.skillRole == SkillRole.SOUGHT }.map { it.name }

        val reviews = reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(userId)
            .filter { !it.comment.isNullOrBlank() }
            .take(10)
            .map { r ->
                ProfileReviewItemDetails(
                    reviewerName = "${r.reviewer?.firstName ?: ""} ${r.reviewer?.lastName ?: ""}".trim(),
                    reviewerAvatarUrl = r.reviewer?.avatarUrl,
                    rating = r.rating,
                    comment = r.comment,
                )
            }

        return ProfileSummaryResult(
            initials = initialsFrom(user.firstName, user.lastName),
            firstName = user.firstName,
            lastName = user.lastName,
            avatarUrl = user.avatarUrl,
            bio = user.bio,
            country = user.country,
            rating = user.rating,
            totalSessions = user.totalSessions,
            totalMatches = user.totalMatches,
            profileVisible = user.profileVisible,
            phone = user.phone,
            skillsOffered = offered,
            skillsSought = sought,
            reviews = reviews,
        )
    }

    @Transactional
    override fun update(userId: Long, dto: ProfileSummaryResult): ProfileSummaryResult {
        val user = userRepository.findById(userId).orElseThrow {
            NoSuchElementException("User with id $userId not found")
        }
        dto.bio?.let { user.bio = it }
        dto.country?.let { user.country = it }
        dto.avatarUrl?.let { user.avatarUrl = it }
        dto.profileVisible?.let { user.profileVisible = it }
        dto.phone?.let { user.phone = it }
        dto.updateFirstName?.let { user.firstName = it }
        dto.updateLastName?.let { user.lastName = it }
        dto.updateTotalSessions?.let { user.totalSessions = it }
        dto.updateRating?.let { user.rating = it }
        user.updatedAt = Date()
        userRepository.save(user)

        if (dto.skillsOffered != null || dto.skillsSought != null) {
            skillRepository.deleteByUserId(userId)
            dto.skillsOffered?.forEach { name ->
                skillRepository.save(
                    Skill(
                        user = user,
                        name = name,
                        skillRole = SkillRole.OFFERED,
                        skillLevel = SkillLevel.BEGINNER
                    )
                )
            }
            dto.skillsSought?.forEach { name ->
                skillRepository.save(
                    Skill(
                        user = user,
                        name = name,
                        skillRole = SkillRole.SOUGHT,
                        skillLevel = SkillLevel.BEGINNER
                    )
                )
            }
        }

        return findByUserId(userId)
    }

    private fun initialsFrom(first: String?, last: String?): String {
        val f = first?.firstOrNull()?.uppercase() ?: ""
        val l = last?.firstOrNull()?.uppercase() ?: ""
        return "$f$l"
    }
}
