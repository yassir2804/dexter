package com.dexter.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "privilege")
data class Privilege(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var name: String,
    @ManyToMany(fetch = FetchType.LAZY)
    var userList: Set<User> = emptySet(),
    @ManyToMany(fetch = FetchType.LAZY)
    var roleList: Set<Role> = emptySet(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Privilege) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Privilege(id=$id, name='$name')"
}
