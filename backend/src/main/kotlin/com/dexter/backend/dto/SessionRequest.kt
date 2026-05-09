package com.dexter.backend.dto

import java.util.Date

data class SessionRequest(
    var matchId: Long? = null,
    var skillId: Long? = null,
    var sessionNumber: Int? = null,
    var title: String? = null,
    var description: String? = null,
    var status: String? = null,
    var scheduledAt: Date? = null,
    var completedAt: Date? = null,
)
