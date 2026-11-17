package com.dexter.backend.controller

import com.dexter.backend.dto.ReviewDetails
import com.dexter.backend.dto.ReviewInput
import com.dexter.backend.service.ReviewService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Reviews (RateScreen).
 *
 *  GET  /v1/reviews?revieweeId=&sessionId=    — lista, filtrable por reviewee o por session
 *  GET  /v1/reviews/{id}                       — detalle
 *  POST /v1/reviews                            — crear (requiere reviewerId, revieweeId, sessionId, rating)
 */
@RestController
@RequestMapping("\${url.reviews}")
class ReviewController(
    private val reviewService: ReviewService,
) {

    @GetMapping
    fun find(
        @RequestParam(required = false) revieweeId: Long?,
        @RequestParam(required = false) sessionId: Long?,
    ): List<ReviewDetails> = when {
        revieweeId != null -> reviewService.findByReviewee(revieweeId)
        sessionId != null -> reviewService.findBySession(sessionId)
        else -> throw IllegalArgumentException("revieweeId or sessionId query param is required")
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ReviewDetails = reviewService.findById(id)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody request: ReviewInput): ReviewDetails = reviewService.create(request)
}
