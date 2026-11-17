package com.dexter.backend.repository

import com.dexter.backend.entity.Session
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SessionRepository : JpaRepository<Session, Long> {
    fun findByMatchIdOrderBySessionNumberAsc(matchId: Long): List<Session>
    fun findBySkillId(skillId: Long): List<Session>
}
