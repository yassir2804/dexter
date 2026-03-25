package com.dexter.app.data.mapper

import com.dexter.app.data.model.ReviewTagDto
import com.dexter.app.domain.model.ReviewTag

/** Mapper for `ReviewTagDto` ↔ `ReviewTag`. */
class ReviewTagMapper {

    fun mapToDomain(dto: ReviewTagDto): ReviewTag = ReviewTag(
        id = dto.id ?: 0L,
        reviewId = dto.reviewId ?: 0L,
        tagName = dto.tagName ?: ""
    )

    fun mapToDto(domain: ReviewTag): ReviewTagDto = ReviewTagDto(
        id = domain.id,
        reviewId = domain.reviewId,
        tagName = domain.tagName
    )
}
