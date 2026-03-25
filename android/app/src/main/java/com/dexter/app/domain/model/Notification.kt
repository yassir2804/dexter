
package com.dexter.app.domain.model

import java.util.Date

/**
 * Push or in-app notification targeted to a user.
 *
 * @property id Notification id.
 * @property userId Target user id.
 * @property referenceId Optional reference to related entity (match, session, message).
 * @property title Short title.
 * @property body Notification body text.
 * @property notificationType Type of notification.
 * @property isRead Whether the user has read it.
 * @property createdAt Timestamp when notification was created.
 */
data class Notification(
    val id: Long,
    val userId: Long,
    val referenceId: Long?,
    val title: String,
    val body: String,
    val notificationType: NotificationType,
    val isRead: Boolean,
    val createdAt: Date
)
