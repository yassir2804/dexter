package com.dexter.backend.dto

import java.util.Date

data class SessionInvitationInput(
    var senderId: Long? = null,
    var receiverId: Long? = null,
    var matchId: Long? = null,
    var expiresAt: Date? = null,
)
