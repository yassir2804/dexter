package com.dexter.backend.entity

import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "availability")
data class Availability(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    var user: User? = null,
    /** 1 = Monday ... 7 = Sunday (ISO-8601). */
    @Column(name = "day_of_week", nullable = false)
    var dayOfWeek: Int,
    @Column(name = "start_time", nullable = false)
    var startTime: LocalTime,
    @Column(name = "end_time", nullable = false)
    var endTime: LocalTime,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Availability) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String =
        "Availability(id=$id, dayOfWeek=$dayOfWeek, startTime=$startTime, endTime=$endTime)"
}
