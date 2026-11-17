package com.dexter.backend.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "review")
data class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "reviewer_id", nullable = false, referencedColumnName = "id")
    var reviewer: User? = null,
    @ManyToOne
    @JoinColumn(name = "reviewee_id", nullable = false, referencedColumnName = "id")
    var reviewee: User? = null,
    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false, referencedColumnName = "id")
    var session: Session? = null,
    @Column(nullable = false)
    var rating: Int,
    var comment: String? = null,
    @Column(name = "created_at")
    var createdAt: Date? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Review) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String =
        "Review(id=$id, reviewerId=${reviewer?.id}, revieweeId=${reviewee?.id}, rating=$rating)"
}
