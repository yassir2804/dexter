package com.dexter.app.data.datasource



import com.dexter.app.domain.model.Match
import com.dexter.app.domain.model.MatchStatus
import java.util.Date

/**
 * In-memory provider for sample Match data used by the repository during development.
 */
class MatchProvider {
    companion object {
        private val sampleMatches = listOf(
            Match(
                id = 1,
                initiatorId = 2,
                receiverId = 1,
                compatibilityScore = 0.86,
                status = MatchStatus.PENDING,
                createdAt = Date()
            ),
            Match(
                id = 2,
                initiatorId = 1,
                receiverId = 3,
                compatibilityScore = 0.74,
                status = MatchStatus.ACCEPTED,
                createdAt = Date()
            ),
            Match(
                id = 3,
                initiatorId = 4,
                receiverId = 1,
                compatibilityScore = 0.69,
                status = MatchStatus.PENDING,
                createdAt = Date()
            ),
            Match(
                id = 4,
                initiatorId = 5,
                receiverId = 1,
                compatibilityScore = 0.92,
                status = MatchStatus.ACCEPTED,
                createdAt = Date()
            ),
            Match(
                id = 5,
                initiatorId = 1,
                receiverId = 6,
                compatibilityScore = 0.55,
                status = MatchStatus.REJECTED,
                createdAt = Date()
            ),
            Match(
                id = 6,
                initiatorId = 7,
                receiverId = 1,
                compatibilityScore = 0.78,
                status = MatchStatus.PENDING,
                createdAt = Date()
            ),
            Match(
                id = 7,
                initiatorId = 8,
                receiverId = 9,
                compatibilityScore = 0.63,
                status = MatchStatus.CANCELLED,
                createdAt = Date()
            ),
            Match(
                id = 8,
                initiatorId = 3,
                receiverId = 1,
                compatibilityScore = 0.81,
                status = MatchStatus.ACCEPTED,
                createdAt = Date()
            ),
            Match(
                id = 9,
                initiatorId = 10,
                receiverId = 1,
                compatibilityScore = 0.47,
                status = MatchStatus.PENDING,
                createdAt = Date()
            )
        )

        fun findMatchesForUser(userId: Long): List<Match> = sampleMatches.filter {
            it.initiatorId == userId || it.receiverId == userId
        }

        fun findAll(): List<Match> = sampleMatches
    }
}
