package com.dexter.app.data.datasource

import com.dexter.app.domain.model.InvitationStatus
import com.dexter.app.domain.model.SessionInvitation
import java.util.Calendar
import java.util.Date

/**
 * In-memory provider for sample SessionInvitation data during development.
 */
class SessionInvitationProvider {
    companion object {

        private fun daysFromNow(days: Int): Date {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, days)
            return cal.time
        }

        private val invitations = listOf(
            SessionInvitation(
                id = 1L,
                senderId = 2L,
                receiverId = 1L,
                matchId = 1L,
                status = InvitationStatus.PENDING,
                expiresAt = daysFromNow(2),
                createdAt = daysFromNow(-1)
            ),
            SessionInvitation(
                id = 2L,
                senderId = 3L,
                receiverId = 1L,
                matchId = 2L,
                status = InvitationStatus.EXPIRED,
                expiresAt = daysFromNow(-3),
                createdAt = daysFromNow(-10)
            )
        )

        fun findById(id: Long): SessionInvitation? = invitations.firstOrNull { it.id == id }

        fun findForUser(userId: Long): List<SessionInvitation> =
            invitations.filter { it.receiverId == userId || it.senderId == userId }
    }
}
