package com.dexter.backend

import com.dexter.backend.entity.Match
import com.dexter.backend.entity.MatchStatus
import com.dexter.backend.entity.Review
import com.dexter.backend.entity.Session
import com.dexter.backend.entity.SessionStatus
import com.dexter.backend.entity.Skill
import com.dexter.backend.entity.SkillLevel
import com.dexter.backend.entity.SkillRole
import com.dexter.backend.entity.User
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
import java.util.Date

/**
 * Tests de integración con CRUD completo (Create, Read, Update, Delete) sobre los
 * 5 repositorios principales contra PostgreSQL real.
 *
 * Anotaciones clave:
 *  - @SpringBootTest          levanta el contexto completo de Spring + DataSource real
 *  - @ActiveProfiles          perfil initlocal con ddl-auto=create
 *  - @Transactional           cada test corre en una transacción que rollback al final
 *  - @Sql                     carga import-database.sql antes de cada test
 *
 * Ejecutar:
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

    // ──────────────────────────────────────────────────────────
    // USER — CRUD
    // ──────────────────────────────────────────────────────────

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
    fun testUserCreate() {
        val newUser = User(
            email = "newuser@dexter.app",
            password = "\$2a\$10\$abcdefghijklmnopqrstuvwx",
            firstName = "New",
            lastName = "User",
            username = "newuser",
            country = "CR",
            enabled = true,
            createDate = Date(),
            updatedAt = Date(),
        )
        val saved = userRepository.save(newUser)
        Assertions.assertNotNull(saved.id)
        Assertions.assertEquals(3, userRepository.findAll().size)
        Assertions.assertTrue(userRepository.findByEmail("newuser@dexter.app").isPresent)
    }

    @Test
    fun testUserUpdate() {
        val user = userRepository.findByEmail("admin@dexter.app").get()
        user.bio = "Updated bio from test"
        user.country = "US"
        user.rating = 4.95
        userRepository.save(user)

        val reloaded = userRepository.findByEmail("admin@dexter.app").get()
        Assertions.assertEquals("Updated bio from test", reloaded.bio)
        Assertions.assertEquals("US", reloaded.country)
        Assertions.assertEquals(4.95, reloaded.rating)
    }

    @Test
    fun testUserDelete() {
        val user = userRepository.findByEmail("user@dexter.app").get()
        // Para respetar las FK NOT NULL, primero borramos las dependencias
        // (skills + sessions + reviews + matches) que referencian este usuario.
        skillRepository.deleteAll(skillRepository.findByUserId(user.id!!))
        val matches = matchRepository.findByInitiatorIdOrReceiverId(user.id!!, user.id!!)
        matches.forEach { m ->
            val sessions = sessionRepository.findByMatchIdOrderBySessionNumberAsc(m.id!!)
            sessions.forEach { s ->
                reviewRepository.deleteAll(reviewRepository.findBySessionId(s.id!!))
            }
            sessionRepository.deleteAll(sessions)
        }
        matchRepository.deleteAll(matches)
        userRepository.deleteById(user.id!!)

        Assertions.assertFalse(userRepository.findByEmail("user@dexter.app").isPresent)
        Assertions.assertEquals(1, userRepository.findAll().size)
    }

    // ──────────────────────────────────────────────────────────
    // SKILL — CRUD
    // ──────────────────────────────────────────────────────────

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
    fun testSkillCreate() {
        val admin = userRepository.findByEmail("admin@dexter.app").get()
        val newSkill = Skill(
            user = admin,
            name = "Piano",
            category = "Music",
            skillRole = SkillRole.OFFERED,
            skillLevel = SkillLevel.INTERMEDIATE,
        )
        val saved = skillRepository.save(newSkill)
        Assertions.assertNotNull(saved.id)
        Assertions.assertEquals(5, skillRepository.findAll().size)
        Assertions.assertEquals(3, skillRepository.findByUserId(admin.id!!).size)
    }

    @Test
    fun testSkillUpdate() {
        val skill = skillRepository.findAll().first { it.name == "Guitar" }
        skill.skillLevel = SkillLevel.BEGINNER
        skill.category = "Strings"
        skillRepository.save(skill)

        val reloaded = skillRepository.findById(skill.id!!).get()
        Assertions.assertEquals(SkillLevel.BEGINNER, reloaded.skillLevel)
        Assertions.assertEquals("Strings", reloaded.category)
    }

    @Test
    fun testSkillDelete() {
        val skill = skillRepository.findAll()
            .first { it.name == "Guitar" && it.skillRole == SkillRole.SOUGHT }
        skillRepository.deleteById(skill.id!!)
        Assertions.assertEquals(3, skillRepository.findAll().size)
        Assertions.assertFalse(skillRepository.findById(skill.id!!).isPresent)
    }

    // ──────────────────────────────────────────────────────────
    // MATCH — CRUD
    // ──────────────────────────────────────────────────────────

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
    fun testMatchCreate() {
        val admin = userRepository.findByEmail("admin@dexter.app").get()
        val user = userRepository.findByEmail("user@dexter.app").get()
        val newMatch = Match(
            initiator = user,
            receiver = admin,
            compatibilityScore = 0.78,
            status = MatchStatus.PENDING,
            createdAt = Date(),
        )
        val saved = matchRepository.save(newMatch)
        Assertions.assertNotNull(saved.id)
        Assertions.assertEquals(2, matchRepository.findAll().size)
        Assertions.assertEquals(MatchStatus.PENDING, saved.status)
    }

    @Test
    fun testMatchUpdate() {
        val match = matchRepository.findAll().first()
        match.status = MatchStatus.REJECTED
        match.compatibilityScore = 0.65
        matchRepository.save(match)

        val reloaded = matchRepository.findById(match.id!!).get()
        Assertions.assertEquals(MatchStatus.REJECTED, reloaded.status)
        Assertions.assertEquals(0.65, reloaded.compatibilityScore)
    }

    @Test
    fun testMatchDelete() {
        val match = matchRepository.findAll().first()
        // Borrar reviews + sessions dependientes para respetar FKs
        val sessions = sessionRepository.findByMatchIdOrderBySessionNumberAsc(match.id!!)
        sessions.forEach { s ->
            reviewRepository.deleteAll(reviewRepository.findBySessionId(s.id!!))
        }
        sessionRepository.deleteAll(sessions)
        matchRepository.deleteById(match.id!!)
        Assertions.assertEquals(0, matchRepository.findAll().size)
    }

    // ──────────────────────────────────────────────────────────
    // SESSION — CRUD
    // ──────────────────────────────────────────────────────────

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
    fun testSessionCreate() {
        val match = matchRepository.findAll().first()
        val skill = skillRepository.findAll().first { it.skillRole == SkillRole.OFFERED }
        val newSession = Session(
            match = match,
            skill = skill,
            sessionNumber = 2,
            title = "Advanced Chords",
            description = "Second session of the plan",
            status = SessionStatus.SCHEDULED,
            scheduledAt = Date(),
            createdAt = Date(),
        )
        val saved = sessionRepository.save(newSession)
        Assertions.assertNotNull(saved.id)
        Assertions.assertEquals(2, sessionRepository.findAll().size)
        Assertions.assertEquals(2, sessionRepository.findByMatchIdOrderBySessionNumberAsc(match.id!!).size)
    }

    @Test
    fun testSessionUpdate() {
        val session = sessionRepository.findAll().first()
        session.status = SessionStatus.COMPLETED
        session.completedAt = Date()
        session.title = "Guitar Basics (rescheduled)"
        sessionRepository.save(session)

        val reloaded = sessionRepository.findById(session.id!!).get()
        Assertions.assertEquals(SessionStatus.COMPLETED, reloaded.status)
        Assertions.assertNotNull(reloaded.completedAt)
        Assertions.assertEquals("Guitar Basics (rescheduled)", reloaded.title)
    }

    @Test
    fun testSessionDelete() {
        val session = sessionRepository.findAll().first()
        // Borrar reviews dependientes primero
        reviewRepository.deleteAll(reviewRepository.findBySessionId(session.id!!))
        sessionRepository.deleteById(session.id!!)
        Assertions.assertEquals(0, sessionRepository.findAll().size)
    }

    // ──────────────────────────────────────────────────────────
    // REVIEW — CRUD
    // ──────────────────────────────────────────────────────────

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

    @Test
    fun testReviewCreate() {
        val admin = userRepository.findByEmail("admin@dexter.app").get()
        val user = userRepository.findByEmail("user@dexter.app").get()
        val session = sessionRepository.findAll().first()
        val newReview = Review(
            reviewer = admin,
            reviewee = user,
            session = session,
            rating = 4,
            comment = "Good student, eager to learn",
            createdAt = Date(),
        )
        val saved = reviewRepository.save(newReview)
        Assertions.assertNotNull(saved.id)
        Assertions.assertEquals(2, reviewRepository.findAll().size)
        Assertions.assertEquals(1, reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(user.id!!).size)
    }

    @Test
    fun testReviewUpdate() {
        val review = reviewRepository.findAll().first()
        review.rating = 3
        review.comment = "Updated review comment"
        reviewRepository.save(review)

        val reloaded = reviewRepository.findById(review.id!!).get()
        Assertions.assertEquals(3, reloaded.rating)
        Assertions.assertEquals("Updated review comment", reloaded.comment)
    }

    @Test
    fun testReviewDelete() {
        val review = reviewRepository.findAll().first()
        reviewRepository.deleteById(review.id!!)
        Assertions.assertEquals(0, reviewRepository.findAll().size)
        Assertions.assertFalse(reviewRepository.findById(review.id!!).isPresent)
    }
}
