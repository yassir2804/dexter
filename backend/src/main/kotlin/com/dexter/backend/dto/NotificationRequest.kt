package com.dexter.backend.dto

data class NotificationRequest(
    var userId: Long? = null,
    var referenceId: Long? = null,
    var title: String? = null,
    var body: String? = null,
    var notificationType: String? = null,
)
