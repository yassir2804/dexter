package com.dexter.backend.entity

import jakarta.persistence.*

@Entity
@Table(name = "skill")
data class Skill(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    var user: User? = null,
    @Column(length = 60)
    var name: String,
    var category: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "skill_role", nullable = false)
    var skillRole: SkillRole,
    @Enumerated(EnumType.STRING)
    @Column(name = "skill_level", nullable = false)
    var skillLevel: SkillLevel,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Skill) return false
        return id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String =
        "Skill(id=$id, name='$name', category='$category', skillRole=$skillRole, skillLevel=$skillLevel)"
}
