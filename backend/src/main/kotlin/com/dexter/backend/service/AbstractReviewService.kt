package com.dexter.backend.service

import com.dexter.backend.dto.ReviewDetails
import com.dexter.backend.dto.ReviewInput
import com.dexter.backend.entity.Notification
import com.dexter.backend.entity.NotificationType
import com.dexter.backend.entity.Review
import com.dexter.backend.entity.ReviewTag
import com.dexter.backend.repository.NotificationRepository
import com.dexter.backend.repository.ReviewRepository
import com.dexter.backend.repository.ReviewTagRepository
import com.dexter.backend.repository.SessionRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Date

@Service
class AbstractReviewService(
    @Autowired val reviewRepository: ReviewRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val sessionRepository: SessionRepository,
    @Autowired val reviewTagRepository: ReviewTagRepository,
    @Autowired val notificationRepository: NotificationRepository,
) : ReviewService {

    override fun findByReviewee(revieweeId: Long): List<ReviewDetails> =
        reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(revieweeId).map(::toDetails)

    override fun findBySession(sessionId: Long): List<ReviewDetails> =
        reviewRepository.findBySessionId(sessionId).map(::toDetails)

    override fun findById(id: Long): ReviewDetails =
        toDetails(reviewRepository.findById(id).orElseThrow {
            NoSuchElementException("Review with id $id not found")
        })

    @Transactional
    override fun create(request: ReviewInput): ReviewDetails {
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

        request.tags.filter { it.isNotBlank() }.distinct().forEach { tag ->
            reviewTagRepository.save(ReviewTag(review = saved, tagName = tag.trim()))
        }

        // Recalcular el rating promedio del reviewee y guardarlo en su perfil.
        val allReviews = reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(revieweeId)
            .mapNotNull { it.rating }
        if (allReviews.isNotEmpty()) {
            val avg = allReviews.sum().toDouble() / allReviews.size
            // Redondear a 1 decimal para no mostrar 4.333333.
            reviewee.rating = Math.round(avg * 10.0) / 10.0
            reviewee.updatedAt = Date()
            userRepository.save(reviewee)
        }

        // Notificar al reviewee que recibio una resena nueva.
        notificationRepository.save(Notification(
            user = reviewee,
            referenceId = saved.id,
            title = "Nueva resena",
            body = "${reviewer.firstName ?: "Alguien"} te dejo una resena de $rating estrellas",
            notificationType = NotificationType.NEW_REVIEW,
            isRead = false,
            createdAt = Date(),
        ))

        return toDetails(saved)
    }

    private fun toDetails(r: Review): ReviewDetails {
        val tags = r.id?.let { id ->
            reviewTagRepository.findByReviewId(id).map { it.tagName }
        } ?: emptyList()
        return ReviewDetails(
            id = r.id,
            reviewerId = r.reviewer?.id,
            reviewerName = "${r.reviewer?.firstName ?: ""} ${r.reviewer?.lastName ?: ""}".trim(),
            reviewerAvatarUrl = r.reviewer?.avatarUrl,
            revieweeId = r.reviewee?.id,
            sessionId = r.session?.id,
            rating = r.rating,
            comment = r.comment,
            createdAt = r.createdAt?.time,
            tags = tags,
        )
    }
}
