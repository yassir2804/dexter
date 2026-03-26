
package com.dexter.app.domain.model

import java.util.Date

/**
 * Invitation to a session sent between users.
 *
 * @property id Invitation identifier.
 * @property senderId Sender user id.
 * @property receiverId Receiver user id.
 * @property matchId Parent match id.
 * @property status Invitation status.
 * @property expiresAt Optional expiration timestamp.
 * @property createdAt Creation timestamp.
 */
data class SessionInvitation(
    val id: Long,
    val senderId: Long,
    val receiverId: Long,
    val matchId: Long,
    val status: InvitationStatus,
    val expiresAt: Date?,
    val createdAt: Date
)
