package com.dexter.backend.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "matches")
data class Match(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false, referencedColumnName = "id")
    var initiator: User? = null,
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false, referencedColumnName = "id")
    var receiver: User? = null,
    @Column(name = "compatibility_score")
    var compatibilityScore: Double? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: MatchStatus = MatchStatus.PENDING,
    @Column(name = "created_at")
    var createdAt: Date? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Match) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String =
        "Match(id=$id, initiatorId=${initiator?.id}, receiverId=${receiver?.id}, compatibilityScore=$compatibilityScore, status=$status)"
}
