package com.dexter.app.data.mapper

import com.dexter.app.data.datasource.model.ReviewDto
import com.dexter.app.domain.model.Review
import java.util.Date
import javax.inject.Inject

/** Mapper for `ReviewDto` ↔ `Review`. */
class ReviewMapper @Inject constructor() {

    fun mapToDomain(dto: ReviewDto): Review = Review(
        id = dto.id ?: 0L,
        reviewerId = dto.reviewerId ?: 0L,
        revieweeId = dto.revieweeId ?: 0L,
        sessionId = dto.sessionId ?: 0L,
        rating = dto.rating ?: 0,
        comment = dto.comment,
        createdAt = Date(dto.createdAt ?: 0L),
        tags = dto.tags,
    )

    fun mapToDto(domain: Review): ReviewDto = ReviewDto(
        id = domain.id,
        reviewerId = domain.reviewerId,
        revieweeId = domain.revieweeId,
        sessionId = domain.sessionId,
        rating = domain.rating,
        comment = domain.comment,
        createdAt = domain.createdAt.time,
        tags = domain.tags,
    )
}
