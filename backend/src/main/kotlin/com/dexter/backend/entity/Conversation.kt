package com.dexter.backend.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "conversation")
data class Conversation(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false, referencedColumnName = "id")
    var user1: User? = null,
    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false, referencedColumnName = "id")
    var user2: User? = null,
    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false, referencedColumnName = "id")
    var match: Match? = null,
    @Column(name = "last_message")
    var lastMessage: String? = null,
    @Column(name = "last_message_at")
    var lastMessageAt: Date? = null,
    @Column(name = "created_at")
    var createdAt: Date? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Conversation) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String =
        "Conversation(id=$id, user1Id=${user1?.id}, user2Id=${user2?.id}, matchId=${match?.id})"
}
