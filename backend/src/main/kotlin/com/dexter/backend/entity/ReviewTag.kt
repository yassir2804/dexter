package com.dexter.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "review_tag")
data class ReviewTag(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false, referencedColumnName = "id")
    var review: Review? = null,
    @Column(name = "tag_name", nullable = false)
    var tagName: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReviewTag) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "ReviewTag(id=$id, tagName='$tagName')"
}
