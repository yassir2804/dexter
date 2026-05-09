package com.dexter.backend.controller

import com.dexter.backend.dto.ReviewTagDetails
import com.dexter.backend.repository.ReviewTagRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * ReviewTags — tags asociados a reviews (Buen profesor, Paciente, etc).
 *
 *  GET /v1/review-tags?reviewId=    — tags de un review específico
 */
@RestController
@RequestMapping("\${url.reviewTags}")
class ReviewTagController(
    private val reviewTagRepository: ReviewTagRepository,
) {

    @GetMapping
    fun findByReview(@RequestParam reviewId: Long): List<ReviewTagDetails> =
        reviewTagRepository.findByReviewId(reviewId).map { t ->
            ReviewTagDetails(
                id = t.id,
                reviewId = t.review?.id,
                tagName = t.tagName,
            )
        }
}
