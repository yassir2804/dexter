package com.dexter.backend.service

import com.dexter.backend.dto.ReviewDetails
import com.dexter.backend.dto.ReviewRequest
import com.dexter.backend.entity.Review
import com.dexter.backend.repository.ReviewRepository
import com.dexter.backend.repository.SessionRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AbstractReviewService(
    @Autowired val reviewRepository: ReviewRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val sessionRepository: SessionRepository,
) : ReviewService {

    override fun findByReviewee(revieweeId: Long): List<ReviewDetails> =
        reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(revieweeId).map(::toDetails)

    override fun findBySession(sessionId: Long): List<ReviewDetails> =
        reviewRepository.findBySessionId(sessionId).map(::toDetails)

    override fun findById(id: Long): ReviewDetails =
        toDetails(reviewRepository.findById(id).orElseThrow {
            NoSuchElementException("Review with id $id not found")
        })

    override fun create(request: ReviewRequest): ReviewDetails {
        val reviewerId = request.reviewerId
            ?: throw IllegalArgumentException("reviewerId is required")
        val revieweeId = request.revieweeId
            ?: throw IllegalArgumentException("revieweeId is required")
        val sessionId = request.sessionId
            ?: throw IllegalArgumentException("sessionId is required")
        val rating = request.rating
            ?: throw IllegalArgumentException("rating is required")
        if (rating !in 1..5) throw IllegalArgumentException("rating must be between 1 and 5")

        val reviewer = userRepository.findById(reviewerId).orElseThrow {
            NoSuchElementException("Reviewer with id $reviewerId not found")
        }
        val reviewee = userRepository.findById(revieweeId).orElseThrow {
            NoSuchElementException("Reviewee with id $revieweeId not found")
        }
        val session = sessionRepository.findById(sessionId).orElseThrow {
            NoSuchElementException("Session with id $sessionId not found")
        }

        val saved = reviewRepository.save(
            Review(
                reviewer = reviewer,
                reviewee = reviewee,
                session = session,
                rating = rating,
                comment = request.comment,
                createdAt = Date(),
            )
        )
        return toDetails(saved)
    }

    private fun toDetails(r: Review): ReviewDetails = ReviewDetails(
        id = r.id,
        reviewerId = r.reviewer?.id,
        reviewerName = "${r.reviewer?.firstName ?: ""} ${r.reviewer?.lastName ?: ""}".trim(),
        reviewerAvatarUrl = r.reviewer?.avatarUrl,
        revieweeId = r.reviewee?.id,
        sessionId = r.session?.id,
        rating = r.rating,
        comment = r.comment,
        createdAt = r.createdAt?.time,
    )
}
