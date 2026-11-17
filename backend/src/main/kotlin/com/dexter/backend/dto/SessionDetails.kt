package com.dexter.backend.dto

data class SessionDetails(
    var id: Long? = null,
    var matchId: Long? = null,
    var skillId: Long? = null,
    var skillName: String? = null,
    var sessionNumber: Int? = null,
    var title: String? = null,
    var description: String? = null,
    var status: String? = null,
    var scheduledAt: Long? = null,
    var completedAt: Long? = null,
    var createdAt: Long? = null,
    var duration: Int? = null,
)
