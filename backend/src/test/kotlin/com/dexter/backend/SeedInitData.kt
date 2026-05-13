package com.dexter.backend

import com.dexter.backend.entity.Availability
import com.dexter.backend.entity.Match
import com.dexter.backend.entity.MatchStatus
import com.dexter.backend.entity.Privilege
import com.dexter.backend.entity.Review
import com.dexter.backend.entity.Role
import com.dexter.backend.entity.Session
import com.dexter.backend.entity.SessionStatus
import com.dexter.backend.entity.Skill
import com.dexter.backend.entity.SkillLevel
import com.dexter.backend.entity.SkillRole
import com.dexter.backend.entity.User
import com.dexter.backend.repository.AvailabilityRepository
import com.dexter.backend.repository.MatchRepository
import com.dexter.backend.repository.PrivilegeRepository
import com.dexter.backend.repository.ReviewRepository
import com.dexter.backend.repository.RoleRepository
import com.dexter.backend.repository.SessionRepository
import com.dexter.backend.repository.SkillRepository
import com.dexter.backend.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import java.time.LocalTime
import java.util.Date

/**
 * Ejecutar con el perfil `initlocal` (ddl-auto=create) para resetear DB y
 * sembrar roles, privilegios, usuarios, skills, matches, sessions y reviews:
 *
 *     ./gradlew test --tests com.dexter.backend.SeedInitData -Dspring.profiles.active=initlocal
 *
 * Escenarios al loguear como admin@dexter.app:
 *   Home  → Solicitudes RECIBIDAS (admin es receiver):
 *             - regularUser → admin (PENDING)
 *             - mariaUser   → admin (PENDING)
 *   Matches → Matches aceptados:
 *             - admin ↔ juanUser  (ACCEPTED)
 *             - anaUser ↔ admin   (ACCEPTED)
 *   Search  → Usuarios sin match con admin (aparecen en resultados):
 *             - carlosUser, luisUser, sofiaUser, andresUser
 *             - pedroUser, gabrielaUser, danielUser
 *   Suggestions → Top compatibles >50% con admin:
 *             - pedroUser   ~90%
 *             - danielUser  ~90%
 *             - gabrielaUser ~72%
 *             - sofiaUser   ~70%
 */
@SpringBootTest
@ActiveProfiles("initlocal")
class SeedInitData {

    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var roleRepository: RoleRepository
    @Autowired lateinit var privilegeRepository: PrivilegeRepository
    @Autowired lateinit var skillRepository: SkillRepository
    @Autowired lateinit var matchRepository: MatchRepository
    @Autowired lateinit var sessionRepository: SessionRepository
    @Autowired lateinit var reviewRepository: ReviewRepository
    @Autowired lateinit var availabilityRepository: AvailabilityRepository

    private val passwordEncoder = BCryptPasswordEncoder()

    @Test
    fun seed() {
        // ── Roles + Privilegios ─────────────────────────────────────────
        val readPrivilege  = privilegeRepository.save(Privilege(name = "READ_PRIVILEGE"))
        val writePrivilege = privilegeRepository.save(Privilege(name = "WRITE_PRIVILEGE"))

        val adminRole = roleRepository.save(
            Role(name = "ROLE_ADMIN", privilegeList = setOf(readPrivilege, writePrivilege))
        )
        val userRole = roleRepository.save(
            Role(name = "ROLE_USER", privilegeList = setOf(readPrivilege))
        )

        // ── Users ───────────────────────────────────────────────────────
        // Admin: ofrece Guitarra+Piano, busca Inglés+Francés, disponible Lun/Mié/Vie
        val adminUser = userRepository.save(User(
            email = "admin@dexter.app",
            password = passwordEncoder.encode("admin123"),
            firstName = "Admin", lastName = "Dexter", username = "admin",
            bio = "Músico profesional con 10 años de experiencia en guitarra y piano.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.8, totalSessions = 12, totalMatches = 5,
            createDate = Date(), updatedAt = Date(), roleList = setOf(adminRole),
        ))

        // regularUser → admin: PENDING (admin receiver → Home)
        val regularUser = userRepository.save(User(
            email = "user@dexter.app",
            password = passwordEncoder.encode("user123"),
            firstName = "Regular", lastName = "User", username = "user",
            bio = "Profesora de inglés y fotógrafa aficionada.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.5, totalSessions = 8, totalMatches = 3,
            createDate = Date(), updatedAt = Date(), roleList = setOf(userRole),
        ))

        // mariaUser → admin: PENDING (admin receiver → Home)
        val mariaUser = userRepository.save(User(
            email = "maria@dexter.app",
            password = passwordEncoder.encode("user123"),
            firstName = "María", lastName = "López", username = "maria",
            bio = "Pianista y aficionada a la cocina. Busco aprender fotografía.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.7, totalSessions = 15, totalMatches = 4,
            createDate = Date(), updatedAt = Date(), roleList = setOf(userRole),
        ))

        // juanUser ↔ admin: ACCEPTED (Matches tab)
        val juanUser = userRepository.save(User(
            email = "juan@dexter.app",
            password = passwordEncoder.encode("user123"),
            firstName = "Juan", lastName = "Pérez", username = "juan",
            bio = "Diseñador gráfico especializado en branding. Quiero aprender piano.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.2, totalSessions = 6, totalMatches = 2,
            createDate = Date(), updatedAt = Date(), roleList = setOf(userRole),
        ))

        // anaUser ↔ admin: ACCEPTED (Matches tab)
        val anaUser = userRepository.save(User(
            email = "ana@dexter.app",
            password = passwordEncoder.encode("user123"),
            firstName = "Ana", lastName = "Ramírez", username = "ana",
            bio = "Instructora de yoga y meditación. Interesada en aprender piano.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.9, totalSessions = 20, totalMatches = 7,
            createDate = Date(), updatedAt = Date(), roleList = setOf(userRole),
        ))

        // Sin match con admin — Search + baja compatibilidad
        val carlosUser = userRepository.save(User(
            email = "carlos@dexter.app",
            password = passwordEncoder.encode("user123"),
            firstName = "Carlos", lastName = "Méndez", username = "carlos",
            bio = "Desarrollador Python especializado en Machine Learning.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.3, totalSessions = 5, totalMatches = 1,
            createDate = Date(), updatedAt = Date(), roleList = setOf(userRole),
        ))

        // Sin match con admin — ofrece Guitarra para Search
        val luisUser = userRepository.save(User(
            email = "luis@dexter.app",
            password = passwordEncoder.encode("user123"),
            firstName = "Luis", lastName = "Vargas", username = "luis",
            bio = "Guitarrista autodidacta con 5 años de práctica. Busco aprender inglés.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.6, totalSessions = 10, totalMatches = 2,
            createDate = Date(), updatedAt = Date(), roleList = setOf(userRole),
        ))

        // Sin match con admin — ofrece Inglés+Francés (suggestion ~70%)
        val sofiaUser = userRepository.save(User(
            email = "sofia@dexter.app",
            password = passwordEncoder.encode("user123"),
            firstName = "Sofía", lastName = "Torres", username = "sofia",
            bio = "Traductora bilingüe inglés-español. Me interesa aprender guitarra.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.4, totalSessions = 7, totalMatches = 3,
            createDate = Date(), updatedAt = Date(), roleList = setOf(userRole),
        ))

        // Sin match con admin — ofrece Piano para Search
        val andresUser = userRepository.save(User(
            email = "andres@dexter.app",
            password = passwordEncoder.encode("user123"),
            firstName = "Andrés", lastName = "Mora", username = "andres",
            bio = "Pianista clásico. Busco aprender fotografía y diseño.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.8, totalSessions = 18, totalMatches = 6,
            createDate = Date(), updatedAt = Date(), roleList = setOf(userRole),
        ))

        // Sin match con admin — suggestion ~90% (ofrece Inglés+Francés, busca Guitarra, días solapan)
        val pedroUser = userRepository.save(User(
            email = "pedro@dexter.app",
            password = passwordEncoder.encode("user123"),
            firstName = "Pedro", lastName = "Alvarado", username = "pedro",
            bio = "Profesor de idiomas con certificación Cambridge. Quiero aprender guitarra.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.7, totalSessions = 14, totalMatches = 5,
            createDate = Date(), updatedAt = Date(), roleList = setOf(userRole),
        ))

        // Sin match con admin — suggestion ~72% (ofrece Inglés, busca Piano+Guitarra, días solapan)
        val gabrielaUser = userRepository.save(User(
            email = "gabriela@dexter.app",
            password = passwordEncoder.encode("user123"),
            firstName = "Gabriela", lastName = "Solís", username = "gabriela",
            bio = "Docente de inglés universitaria. Me encantaría aprender piano y guitarra.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.5, totalSessions = 9, totalMatches = 3,
            createDate = Date(), updatedAt = Date(), roleList = setOf(userRole),
        ))

        // Sin match con admin — suggestion ~90% (ofrece Inglés+Francés, busca Piano, días solapan)
        val danielUser = userRepository.save(User(
            email = "daniel@dexter.app",
            password = passwordEncoder.encode("user123"),
            firstName = "Daniel", lastName = "Rojas", username = "daniel",
            bio = "Bilingüe francés-español. Siempre quise aprender piano.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.6, totalSessions = 11, totalMatches = 4,
            createDate = Date(), updatedAt = Date(), roleList = setOf(userRole),
        ))

        // ── Skills ──────────────────────────────────────────────────────
        skillRepository.saveAll(listOf(
            // Admin
            Skill(user = adminUser, name = "Guitarra",  category = "Música",       skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = adminUser, name = "Piano",     category = "Música",       skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = adminUser, name = "Inglés",    category = "Idiomas",      skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = adminUser, name = "Francés",   category = "Idiomas",      skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            // Regular (busca Guitarra, ofrece Inglés — compat moderada)
            Skill(user = regularUser, name = "Inglés",     category = "Idiomas",      skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = regularUser, name = "Fotografía", category = "Arte",         skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = regularUser, name = "Guitarra",   category = "Música",       skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = regularUser, name = "Kotlin",     category = "Programación", skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            // María
            Skill(user = mariaUser, name = "Piano",      category = "Música",       skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = mariaUser, name = "Cocina",     category = "Gastronomía",  skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = mariaUser, name = "Fotografía", category = "Arte",         skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = mariaUser, name = "Guitarra",   category = "Música",       skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            // Juan
            Skill(user = juanUser, name = "Diseño Gráfico", category = "Diseño",   skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = juanUser, name = "Photoshop",      category = "Diseño",   skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = juanUser, name = "Piano",          category = "Música",   skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = juanUser, name = "Guitarra",       category = "Música",   skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            // Ana
            Skill(user = anaUser, name = "Yoga",       category = "Bienestar",    skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = anaUser, name = "Meditación", category = "Bienestar",    skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = anaUser, name = "Piano",      category = "Música",       skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            // Carlos (baja compat con admin — días no solapan)
            Skill(user = carlosUser, name = "Python",           category = "Programación", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = carlosUser, name = "Machine Learning", category = "Programación", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = carlosUser, name = "Piano",            category = "Música",       skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = carlosUser, name = "Guitarra",         category = "Música",       skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            // Luis (ofrece Guitarra → Search; compat baja porque no enseña inglés/francés)
            Skill(user = luisUser, name = "Guitarra", category = "Música",  skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = luisUser, name = "Bajo",     category = "Música",  skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.BEGINNER),
            Skill(user = luisUser, name = "Inglés",   category = "Idiomas", skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            // Sofía (ofrece Inglés+Francés, busca Guitarra → suggestion ~70%)
            Skill(user = sofiaUser, name = "Inglés",  category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = sofiaUser, name = "Francés", category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = sofiaUser, name = "Guitarra",category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            // Andrés (ofrece Piano → Search)
            Skill(user = andresUser, name = "Piano",          category = "Música", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = andresUser, name = "Teoría Musical", category = "Música", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = andresUser, name = "Fotografía",     category = "Arte",   skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            // Pedro (ofrece Inglés+Francés, busca Guitarra, días Lun+Mié → suggestion ~90%)
            Skill(user = pedroUser, name = "Inglés",  category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = pedroUser, name = "Francés", category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = pedroUser, name = "Guitarra",category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            // Gabriela (ofrece Inglés, busca Piano+Guitarra, días Mié+Vie → suggestion ~72%)
            Skill(user = gabrielaUser, name = "Inglés",  category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = gabrielaUser, name = "Piano",   category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = gabrielaUser, name = "Guitarra",category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            // Daniel (ofrece Francés+Inglés, busca Piano, días Lun+Vie → suggestion ~90%)
            Skill(user = danielUser, name = "Francés", category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = danielUser, name = "Inglés",  category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = danielUser, name = "Piano",   category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
        ))

        // ── Availability (ISO-8601: 1=Lun … 7=Dom) ─────────────────────
        availabilityRepository.saveAll(listOf(
            // Admin: Lun, Mié, Vie
            Availability(user = adminUser, dayOfWeek = 1, startTime = LocalTime.of(18, 0), endTime = LocalTime.of(20, 0)),
            Availability(user = adminUser, dayOfWeek = 3, startTime = LocalTime.of(18, 0), endTime = LocalTime.of(20, 0)),
            Availability(user = adminUser, dayOfWeek = 5, startTime = LocalTime.of(16, 0), endTime = LocalTime.of(18, 0)),
            // Regular: Mar, Jue
            Availability(user = regularUser, dayOfWeek = 2, startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0)),
            Availability(user = regularUser, dayOfWeek = 4, startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0)),
            // María: Lun, Jue, Sáb
            Availability(user = mariaUser, dayOfWeek = 1, startTime = LocalTime.of(9, 0),  endTime = LocalTime.of(11, 0)),
            Availability(user = mariaUser, dayOfWeek = 4, startTime = LocalTime.of(15, 0), endTime = LocalTime.of(17, 0)),
            Availability(user = mariaUser, dayOfWeek = 6, startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0)),
            // Juan: Lun, Mié (solapa con admin → match aceptado tiene sentido)
            Availability(user = juanUser, dayOfWeek = 1, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),
            Availability(user = juanUser, dayOfWeek = 3, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),
            // Ana: Lun, Mié, Sáb
            Availability(user = anaUser, dayOfWeek = 1, startTime = LocalTime.of(7, 0),  endTime = LocalTime.of(9, 0)),
            Availability(user = anaUser, dayOfWeek = 3, startTime = LocalTime.of(7, 0),  endTime = LocalTime.of(9, 0)),
            Availability(user = anaUser, dayOfWeek = 6, startTime = LocalTime.of(9, 0),  endTime = LocalTime.of(11, 0)),
            // Carlos: Mar, Jue, Dom (sin solapamiento con admin)
            Availability(user = carlosUser, dayOfWeek = 2, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),
            Availability(user = carlosUser, dayOfWeek = 4, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),
            Availability(user = carlosUser, dayOfWeek = 7, startTime = LocalTime.of(14, 0), endTime = LocalTime.of(16, 0)),
            // Luis: Lun, Mié
            Availability(user = luisUser, dayOfWeek = 1, startTime = LocalTime.of(17, 0), endTime = LocalTime.of(19, 0)),
            Availability(user = luisUser, dayOfWeek = 3, startTime = LocalTime.of(17, 0), endTime = LocalTime.of(19, 0)),
            // Sofía: Mar, Jue, Sáb (sin solapamiento con admin → solo skills aportan)
            Availability(user = sofiaUser, dayOfWeek = 2, startTime = LocalTime.of(9, 0), endTime = LocalTime.of(11, 0)),
            Availability(user = sofiaUser, dayOfWeek = 4, startTime = LocalTime.of(9, 0), endTime = LocalTime.of(11, 0)),
            Availability(user = sofiaUser, dayOfWeek = 6, startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0)),
            // Andrés: Vie, Dom
            Availability(user = andresUser, dayOfWeek = 5, startTime = LocalTime.of(16, 0), endTime = LocalTime.of(18, 0)),
            Availability(user = andresUser, dayOfWeek = 7, startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0)),
            // Pedro: Lun, Mié (solapa con admin → boost por disponibilidad)
            Availability(user = pedroUser, dayOfWeek = 1, startTime = LocalTime.of(18, 0), endTime = LocalTime.of(20, 0)),
            Availability(user = pedroUser, dayOfWeek = 3, startTime = LocalTime.of(18, 0), endTime = LocalTime.of(20, 0)),
            // Gabriela: Mié, Vie (solapa con admin)
            Availability(user = gabrielaUser, dayOfWeek = 3, startTime = LocalTime.of(17, 0), endTime = LocalTime.of(19, 0)),
            Availability(user = gabrielaUser, dayOfWeek = 5, startTime = LocalTime.of(15, 0), endTime = LocalTime.of(17, 0)),
            // Daniel: Lun, Vie (solapa con admin)
            Availability(user = danielUser, dayOfWeek = 1, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),
            Availability(user = danielUser, dayOfWeek = 5, startTime = LocalTime.of(16, 0), endTime = LocalTime.of(18, 0)),
        ))

        // ── Matches ─────────────────────────────────────────────────────
        // 1. regular → admin: PENDING (admin receiver → Home "Solicitudes recibidas")
        matchRepository.save(Match(
            initiator = regularUser, receiver = adminUser,
            compatibilityScore = 78.0, status = MatchStatus.PENDING, createdAt = Date(),
        ))

        // 2. maria → admin: PENDING (admin receiver → Home "Solicitudes recibidas")
        matchRepository.save(Match(
            initiator = mariaUser, receiver = adminUser,
            compatibilityScore = 85.0, status = MatchStatus.PENDING, createdAt = Date(),
        ))

        // 3. admin → juan: ACCEPTED (aparece en Matches de admin)
        val matchJuan = matchRepository.save(Match(
            initiator = adminUser, receiver = juanUser,
            compatibilityScore = 62.0, status = MatchStatus.ACCEPTED, createdAt = Date(),
        ))

        // 4. ana → admin: ACCEPTED (aparece en Matches de admin)
        matchRepository.save(Match(
            initiator = anaUser, receiver = adminUser,
            compatibilityScore = 70.0, status = MatchStatus.ACCEPTED, createdAt = Date(),
        ))

        // ── Sesión completada (match admin↔juan) ────────────────────────
        val guitarraSkill = skillRepository.findByUserId(adminUser.id!!)
            .first { it.skillRole == SkillRole.OFFERED && it.name == "Guitarra" }

        val session1 = sessionRepository.save(Session(
            match = matchJuan,
            skill = guitarraSkill,
            sessionNumber = 1,
            title = "Introducción a la guitarra",
            description = "Primera clase — acordes básicos",
            status = SessionStatus.COMPLETED,
            scheduledAt = Date(), completedAt = Date(), createdAt = Date(),
        ))

        // ── Review ──────────────────────────────────────────────────────
        reviewRepository.save(Review(
            reviewer = juanUser,
            reviewee = adminUser,
            session = session1,
            rating = 5,
            comment = "Excelente profesor, muy paciente y claro.",
            createdAt = Date(),
        ))

        println(
            "Seeded: ${privilegeRepository.count()} privileges, ${roleRepository.count()} roles, " +
            "${userRepository.count()} users, ${skillRepository.count()} skills, " +
            "${availabilityRepository.count()} availability entries, " +
            "${matchRepository.count()} matches, ${sessionRepository.count()} sessions, " +
            "${reviewRepository.count()} reviews"
        )
    }
}
