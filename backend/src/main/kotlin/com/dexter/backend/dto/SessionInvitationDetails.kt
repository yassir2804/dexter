package com.dexter.backend.dto

data class SessionInvitationDetails(
    var id: Long? = null,
    var senderId: Long? = null,
    var senderName: String? = null,
    var receiverId: Long? = null,
    var receiverName: String? = null,
    var matchId: Long? = null,
    var status: String? = null,
    var expiresAt: Long? = null,
    var createdAt: Long? = null,
)
