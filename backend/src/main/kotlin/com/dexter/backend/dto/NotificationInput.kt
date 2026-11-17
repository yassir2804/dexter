package com.dexter.backend.dto

data class NotificationInput(
    var userId: Long? = null,
    var referenceId: Long? = null,
    var title: String? = null,
    var body: String? = null,
    var notificationType: String? = null,
)
