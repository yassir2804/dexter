package com.dexter.app.domain.model

import java.util.Date

/**
 * Represents a match between two users with a compatibility score.
 *
 * @property id Match identifier.
 * @property initiatorId User who initiated the match.
 * @property receiverId User who received the match.
 * @property compatibilityScore Numeric score expressing compatibility.
 * @property status Match lifecycle status.
 * @property createdAt Timestamp when match was created.
 */
data class Match(
    val id: Long,
    val initiatorId: Long,
    val receiverId: Long,
    val compatibilityScore: Double,
    val status: MatchStatus,
    val createdAt: Date
)
