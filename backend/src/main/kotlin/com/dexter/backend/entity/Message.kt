package com.dexter.backend.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "message")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false, referencedColumnName = "id")
    var conversation: Conversation? = null,
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false, referencedColumnName = "id")
    var sender: User? = null,
    @Column(nullable = false, columnDefinition = "TEXT")
    var text: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: MessageStatus = MessageStatus.SENT,
    @Column(name = "created_at")
    var createdAt: Date? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Message) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String =
        "Message(id=$id, conversationId=${conversation?.id}, senderId=${sender?.id}, status=$status)"
}
