package com.dexter.backend.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "session_invitation")
data class SessionInvitation(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false, referencedColumnName = "id")
    var sender: User? = null,
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false, referencedColumnName = "id")
    var receiver: User? = null,
    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false, referencedColumnName = "id")
    var match: Match? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: InvitationStatus = InvitationStatus.PENDING,
    @Column(name = "expires_at")
    var expiresAt: Date? = null,
    @Column(name = "created_at")
    var createdAt: Date? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SessionInvitation) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String =
        "SessionInvitation(id=$id, senderId=${sender?.id}, receiverId=${receiver?.id}, status=$status)"
}
