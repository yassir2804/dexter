package com.dexter.backend.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "notification")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    var user: User? = null,
    /** ID del recurso relacionado (match, session, message, review...). */
    @Column(name = "reference_id")
    var referenceId: Long? = null,
    var title: String,
    var body: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    var notificationType: NotificationType,
    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,
    @Column(name = "created_at")
    var createdAt: Date? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Notification) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String =
        "Notification(id=$id, userId=${user?.id}, type=$notificationType, isRead=$isRead, title='$title')"
}
