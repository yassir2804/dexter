package com.dexter.backend.entity

import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var email: String? = null,
    var password: String? = null,
    @Column(name = "first_name")
    var firstName: String? = null,
    @Column(name = "last_name")
    var lastName: String? = null,
    var username: String? = null,
    var bio: String? = null,
    @Column(name = "avatar_url", columnDefinition = "TEXT")
    var avatarUrl: String? = null,
    var country: String? = null,
    var enabled: Boolean = true,
    @Column(name = "profile_visible")
    var profileVisible: Boolean? = true,
    @Column(name = "token_expired")
    var tokenExpired: Boolean? = false,
    var rating: Double? = 0.0,
    @Column(name = "total_sessions")
    var totalSessions: Int? = 0,
    @Column(name = "total_matches")
    var totalMatches: Int? = 0,
    @Column(name = "create_date")
    var createDate: Date? = null,
    @Column(name = "updated_at")
    var updatedAt: Date? = null,
    var phone: String? = null,
    @ManyToMany
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roleList: Set<Role>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        if (id != other.id) return false
        if (email != other.email) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (email?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String =
        "User(id=$id, email='$email', firstName='$firstName', lastName='$lastName', username='$username')"
}
