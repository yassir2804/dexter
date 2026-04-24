package com.dexter.backend

import com.dexter.backend.repository.MatchRepository
import com.dexter.backend.repository.ReviewRepository
import com.dexter.backend.repository.SessionRepository
import com.dexter.backend.repository.SkillRepository
import com.dexter.backend.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.transaction.annotation.Transactional

/**
 * Carga datos de prueba y valida los repositorios principales.
 *
 * Ejecutar con perfil initlocal (ddl-auto=create — resetea DB y siembra datos):
 *
 *     ./gradlew test --tests com.dexter.backend.LoadInitData
 */
@ActiveProfiles("initlocal")
@SpringBootTest
@Transactional
@Sql("/import-database.sql")
class LoadInitData(
    @Autowired val userRepository: UserRepository,
    @Autowired val skillRepository: SkillRepository,
    @Autowired val matchRepository: MatchRepository,
    @Autowired val sessionRepository: SessionRepository,
    @Autowired val reviewRepository: ReviewRepository,
) {

    @Test
    fun testUserFindAll() {
        val users = userRepository.findAll()
        Assertions.assertEquals(2, users.size)
    }

    @Test
    fun testUserFindByEmail() {
        val user = userRepository.findByEmail("admin@dexter.app")
        Assertions.assertTrue(user.isPresent)
        Assertions.assertEquals("Admin", user.get().firstName)
    }

    @Test
    fun testSkillFindAll() {
        val skills = skillRepository.findAll()
        Assertions.assertEquals(4, skills.size)
    }

    @Test
    fun testSkillFindByUserId() {
        val user = userRepository.findByEmail("admin@dexter.app").get()
        val skills = skillRepository.findByUserId(user.id!!)
        Assertions.assertEquals(2, skills.size)
    }

    @Test
    fun testMatchFindAll() {
        val matches = matchRepository.findAll()
        Assertions.assertEquals(1, matches.size)
    }

    @Test
    fun testMatchFindByUsers() {
        val user = userRepository.findByEmail("admin@dexter.app").get()
        val matches = matchRepository.findByInitiatorIdOrReceiverId(user.id!!, user.id!!)
        Assertions.assertTrue(matches.isNotEmpty())
    }

    @Test
    fun testSessionFindAll() {
        val sessions = sessionRepository.findAll()
        Assertions.assertEquals(1, sessions.size)
    }

    @Test
    fun testSessionFindByMatch() {
        val match = matchRepository.findAll().first()
        val sessions = sessionRepository.findByMatchIdOrderBySessionNumberAsc(match.id!!)
        Assertions.assertEquals(1, sessions.size)
        Assertions.assertEquals("Guitar Basics", sessions.first().title)
    }

    @Test
    fun testReviewFindAll() {
        val reviews = reviewRepository.findAll()
        Assertions.assertEquals(1, reviews.size)
    }

    @Test
    fun testReviewFindByReviewee() {
        val admin = userRepository.findByEmail("admin@dexter.app").get()
        val reviews = reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(admin.id!!)
        Assertions.assertEquals(1, reviews.size)
        Assertions.assertEquals(5, reviews.first().rating)
    }
}
