
package com.dexter.app.domain.model

import java.util.Date
import com.dexter.app.domain.model.MessageStatus

/**
 * Message sent within a conversation.
 *
 * @property id Message identifier.
 * @property conversationId Parent conversation id.
 * @property senderId Sender user id.
 * @property text Message text payload.
 * @property status Delivery/read status.
 * @property createdAt Creation timestamp.
 */
data class Message(
    val id: Long,
    val conversationId: Long,
    val senderId: Long,
    val text: String,
    val status: MessageStatus,
    val createdAt: Date
)
