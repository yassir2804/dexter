package com.dexter.backend.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class NotificationDetails(
    var id: Long? = null,
    var userId: Long? = null,
    var referenceId: Long? = null,
    var title: String? = null,
    var body: String? = null,
    var notificationType: String? = null,
    @get:JsonProperty("isRead")
    var isRead: Boolean = false,
    var createdAt: Long? = null,
)
