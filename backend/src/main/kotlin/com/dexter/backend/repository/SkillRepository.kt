package com.dexter.backend.repository

import com.dexter.backend.entity.Skill
import com.dexter.backend.entity.SkillRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillRepository : JpaRepository<Skill, Long> {
    fun findByUserId(userId: Long): List<Skill>
    fun findByUserIdAndSkillRole(userId: Long, skillRole: SkillRole): List<Skill>
    fun findByNameContainingIgnoreCase(name: String): List<Skill>
    fun findByCategoryIgnoreCase(category: String): List<Skill>
}
