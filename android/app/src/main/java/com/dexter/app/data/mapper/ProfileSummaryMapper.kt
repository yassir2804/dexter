package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.ProfileReviewItemDto
import com.dexter.app.data.datasource.model.ProfileSummaryDto
import com.dexter.app.domain.model.ProfileReviewItem
import com.dexter.app.domain.model.ProfileSummary
import javax.inject.Inject

/** Mapper for ProfileSummaryDto <-> ProfileSummary. */
class ProfileSummaryMapper @Inject constructor() {

    fun mapToDomain(dto: ProfileSummaryDto): ProfileSummary = ProfileSummary(
        initials = dto.initials ?: "",
        firstName = dto.firstName ?: "",
        lastName = dto.lastName ?: "",
        avatarUrl = dto.avatarUrl,
        bio = dto.bio ?: "",
        country = dto.country,
        phone = dto.phone,
        rating = dto.rating ?: 0.0,
        totalSessions = dto.totalSessions ?: 0,
        totalMatches = dto.totalMatches ?: 0,
        profileVisible = dto.profileVisible ?: true,
        skillsOffered = dto.skillsOffered ?: emptyList(),
        skillsSought = dto.skillsSought ?: emptyList(),
        reviews = dto.reviews?.map { mapReviewToDomain(it) } ?: emptyList()
    )

    private fun mapReviewToDomain(dto: ProfileReviewItemDto): ProfileReviewItem = ProfileReviewItem(
        reviewerName = dto.reviewerName ?: "Usuario",
        reviewerAvatarUrl = dto.reviewerAvatarUrl,
        rating = dto.rating ?: 0,
        comment = dto.comment ?: ""
    )

    fun mapToDto(domain: ProfileSummary): ProfileSummaryDto = ProfileSummaryDto(
        initials = domain.initials,
        firstName = domain.firstName,
        lastName = domain.lastName,
        avatarUrl = domain.avatarUrl,
        bio = domain.bio,
        country = domain.country,
        phone = domain.phone,
        rating = domain.rating,
        totalSessions = domain.totalSessions,
        totalMatches = domain.totalMatches,
        profileVisible = domain.profileVisible,
        updateFirstName = domain.firstName,
        updateLastName = domain.lastName,
        skillsOffered = domain.skillsOffered,
        skillsSought = domain.skillsSought,
        reviews = domain.reviews.map { mapReviewToDto(it) }
    )

    private fun mapReviewToDto(domain: ProfileReviewItem): ProfileReviewItemDto = ProfileReviewItemDto(
        reviewerName = domain.reviewerName,
        reviewerAvatarUrl = domain.reviewerAvatarUrl,
        rating = domain.rating,
        comment = domain.comment
    )
}
