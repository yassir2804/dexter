package com.dexter.app.domain.model

import java.util.Date
import com.dexter.app.domain.model.SessionStatus

/**
 * Domain representation of a session within a match.
 *
 * @property id Session identifier.
 * @property matchId Parent match id.
 * @property skillId Related skill id used in the session.
 * @property sessionNumber Ordinal session number (e.g. session 1 of 5).
 * @property title Short session title.
 * @property description Longer details.
 * @property status Session lifecycle status.
 * @property scheduledAt Optional scheduled timestamp.
 * @property completedAt Optional completion timestamp.
 * @property createdAt Creation timestamp.
 */
data class Session(
    val id: Long,
    val matchId: Long,
    val skillId: Long,
    val sessionNumber: Int,
    val title: String,
    val description: String,
    val status: SessionStatus,
    val scheduledAt: Date?,
    val completedAt: Date?,
    val createdAt: Date
)
