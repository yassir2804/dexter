package com.dexter.backend.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "session")
data class Session(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "match_id", nullable = false, referencedColumnName = "id")
    var match: Match? = null,
    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false, referencedColumnName = "id")
    var skill: Skill? = null,
    @Column(name = "session_number", nullable = false)
    var sessionNumber: Int,
    var title: String,
    var description: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SessionStatus = SessionStatus.SCHEDULED,
    @Column(name = "scheduled_at")
    var scheduledAt: Date? = null,
    @Column(name = "completed_at")
    var completedAt: Date? = null,
    var duration: Int? = null,
    @Column(name = "created_at")
    var createdAt: Date? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Session) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String =
        "Session(id=$id, matchId=${match?.id}, sessionNumber=$sessionNumber, title='$title', status=$status)"
}
