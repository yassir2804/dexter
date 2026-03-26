package com.dexter.app.data.datasource.model

/** DTO for Message. */
data class MessageDto(
    val id: Long? = null,
    val conversationId: Long? = null,
    val senderId: Long? = null,
    val text: String? = null,
    val status: String? = null,
    val createdAt: Long? = null
)
