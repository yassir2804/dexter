package com.dexter.backend

import com.dexter.backend.entity.InvitationStatus
import com.dexter.backend.repository.AvailabilityRepository
import com.dexter.backend.repository.ConversationRepository
import com.dexter.backend.repository.MatchRepository
import com.dexter.backend.repository.MessageRepository
import com.dexter.backend.repository.NotificationRepository
import com.dexter.backend.repository.PrivilegeRepository
import com.dexter.backend.repository.ReviewRepository
import com.dexter.backend.repository.ReviewTagRepository
import com.dexter.backend.repository.RoleRepository
import com.dexter.backend.repository.SessionInvitationRepository
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
    @Autowired val roleRepository: RoleRepository,
    @Autowired val privilegeRepository: PrivilegeRepository,
    @Autowired val availabilityRepository: AvailabilityRepository,
    @Autowired val conversationRepository: ConversationRepository,
    @Autowired val messageRepository: MessageRepository,
    @Autowired val notificationRepository: NotificationRepository,
    @Autowired val sessionInvitationRepository: SessionInvitationRepository,
    @Autowired val reviewTagRepository: ReviewTagRepository,
) {

    // ───────── User ─────────

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

    // ───────── Skill ─────────

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

    // ───────── Match ─────────

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

    // ───────── Session ─────────

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

    // ───────── Review ─────────

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

    // ───────── Role ─────────

    @Test
    fun testRoleFindAll() {
        val roles = roleRepository.findAll()
        Assertions.assertEquals(2, roles.size)
    }

    @Test
    fun testRoleFindByName() {
        val role = roleRepository.findByName("ROLE_ADMIN")
        Assertions.assertTrue(role.isPresent)
        Assertions.assertEquals("ROLE_ADMIN", role.get().name)
    }

    // ───────── Privilege ─────────

    @Test
    fun testPrivilegeFindAll() {
        val privileges = privilegeRepository.findAll()
        Assertions.assertEquals(2, privileges.size)
    }

    @Test
    fun testPrivilegeFindByName() {
        val privilege = privilegeRepository.findByName("READ_PRIVILEGE")
        Assertions.assertTrue(privilege.isPresent)
    }

    // ───────── Availability ─────────

    @Test
    fun testAvailabilityFindAll() {
        val availabilities = availabilityRepository.findAll()
        Assertions.assertEquals(2, availabilities.size)
    }

    @Test
    fun testAvailabilityFindByUserId() {
        val admin = userRepository.findByEmail("admin@dexter.app").get()
        val availabilities = availabilityRepository.findByUserId(admin.id!!)
        Assertions.assertEquals(2, availabilities.size)
    }

    // ───────── Conversation ─────────

    @Test
    fun testConversationFindAll() {
        val conversations = conversationRepository.findAll()
        Assertions.assertEquals(1, conversations.size)
    }

    @Test
    fun testConversationFindByMatchId() {
        val match = matchRepository.findAll().first()
        val conversation = conversationRepository.findByMatchId(match.id!!)
        Assertions.assertNotNull(conversation)
    }

    // ───────── Message ─────────

    @Test
    fun testMessageFindAll() {
        val messages = messageRepository.findAll()
        Assertions.assertEquals(2, messages.size)
    }

    @Test
    fun testMessageFindByConversation() {
        val conversation = conversationRepository.findAll().first()
        val messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.id!!)
        Assertions.assertEquals(2, messages.size)
    }

    // ───────── Notification ─────────

    @Test
    fun testNotificationFindAll() {
        val notifications = notificationRepository.findAll()
        Assertions.assertEquals(2, notifications.size)
    }

    @Test
    fun testNotificationFindByUserIdAndIsRead() {
        val admin = userRepository.findByEmail("admin@dexter.app").get()
        val unread = notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(admin.id!!, false)
        Assertions.assertEquals(1, unread.size)
        Assertions.assertEquals("Nuevo match!", unread.first().title)
    }

    // ───────── Session Invitation ─────────

    @Test
    fun testSessionInvitationFindAll() {
        val invitations = sessionInvitationRepository.findAll()
        Assertions.assertEquals(1, invitations.size)
    }

    @Test
    fun testSessionInvitationFindByReceiverAndStatus() {
        val receiver = userRepository.findByEmail("user@dexter.app").get()
        val invitations = sessionInvitationRepository.findByReceiverIdAndStatus(
            receiver.id!!,
            InvitationStatus.PENDING,
        )
        Assertions.assertEquals(1, invitations.size)
    }

    // ───────── Review Tag ─────────

    @Test
    fun testReviewTagFindAll() {
        val tags = reviewTagRepository.findAll()
        Assertions.assertEquals(2, tags.size)
    }

    @Test
    fun testReviewTagFindByReviewId() {
        val review = reviewRepository.findAll().first()
        val tags = reviewTagRepository.findByReviewId(review.id!!)
        Assertions.assertEquals(2, tags.size)
    }
}
