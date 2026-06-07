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
 * Seed de datos de prueba con scores de compatibilidad CONOCIDOS para testing funcional.
 *
 * Ejecutar con el perfil `initlocal` (ddl-auto=create) para resetear DB:
 *     ./gradlew test --tests com.dexter.backend.SeedInitData -Dspring.profiles.active=initlocal
 *
 * ── SCORES ESPERADOS (TU1 = admin como referencia) ──
 *   TU1 ↔ TU2  (Bruno)    = 100.0%  ← skills perfect + días solapan
 *   TU1 ↔ TU5  (Elena)    =  76.7%  ← skills parcial + días solapan
 *   TU1 ↔ TU3  (Carla)    =  70.0%  ← skills perfect + SIN solapamiento de días
 *   TU1 ↔ TU7  (Gabriela) =  55.0%  ← skills parcial + 2/3 días solapan
 *   TU1 ↔ TU4  (David)    =  30.0%  ← SIN skills + días solapan → NO sugerencia
 *   TU1 ↔ TU6  (Felipe)   =   0.0%  ← SIN skills + SIN días      → NO sugerencia
 *
 * ── MATCHES PRE-ARMADOS ──
 *   M1: TU2 (Bruno)  → TU1 (Ana):   PENDING  — para probar solicitudes recibidas
 *   M2: TU3 (Carla)  → TU1 (Ana):   PENDING  — segunda solicitud para probar rechazo
 *   M3: TU1 (Ana)    ↔ TU5 (Elena): ACCEPTED — 6 sesiones, 0 completadas
 *   M4: TU1 (Ana)    ↔ TU8 (Hugo):  ACCEPTED — 6 sesiones, 1 completada + review
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

        // ── USUARIOS DE PRUEBA CON SCORES CONOCIDOS ─────────────────────

        // TU1 — Ana (cuenta principal de prueba)
        // Ofrece: Guitarra + Piano | Busca: Inglés + Francés | Avail: Lun/Mié/Vie
        val adminUser = userRepository.save(User(
            email = "admin@dexter.app", password = passwordEncoder.encode("admin123"),
            firstName = "Ana", lastName = "Guitarra", username = "admin",
            bio = "Músico profesional. Toco guitarra y piano. Quiero aprender idiomas.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.8, totalSessions = 12, totalMatches = 5,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0001",
            roleList = setOf(userRole),
        ))

        // TU9 — Admin real del sistema
        val systemAdmin = userRepository.save(User(
            email = "admindexter@dexter.app", password = passwordEncoder.encode("admin123"),
            firstName = "Admin", lastName = "Sistema", username = "admindexter",
            bio = "Administrador del sistema Dexter.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 0.0, totalSessions = 0, totalMatches = 0,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0000",
            roleList = setOf(adminRole),
        ))

        // TU2 — Bruno (100% con TU1)
        // Ofrece: Inglés + Francés | Busca: Guitarra + Piano | Avail: Lun/Mié/Vie
        // Score: skillScore=(2+2)/4=1.0, availScore=3/3=1.0 → 100%
        val brunoUser = userRepository.save(User(
            email = "user@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "Bruno", lastName = "Idiomas", username = "user",
            bio = "Bilingüe inglés-francés. Quiero aprender guitarra y piano.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.5, totalSessions = 8, totalMatches = 3,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0002",
            roleList = setOf(userRole),
        ))

        // TU3 — Carla (70% con TU1 — skills perfectas, SIN solapamiento de días)
        // Ofrece: Inglés + Francés | Busca: Guitarra + Piano | Avail: Mar/Sáb
        // Score: skillScore=(2+2)/4=1.0, availScore=0/5=0.0 → 70%
        val carlaUser = userRepository.save(User(
            email = "carla@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "Carla", lastName = "Sin Horario", username = "carla",
            bio = "Profesora de idiomas. Disponible fines de semana y martes.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.2, totalSessions = 5, totalMatches = 2,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0013",
            roleList = setOf(userRole),
        ))

        // TU4 — David (30% con TU1 — SIN skills match, días solapan)
        // Ofrece: Cocina + Yoga | Busca: Fotografía + Diseño | Avail: Lun/Mié/Vie
        // Score: skillScore=(0+0)/4=0.0, availScore=3/3=1.0 → 30% → NO aparece en sugerencias
        val davidUser = userRepository.save(User(
            email = "david@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "David", lastName = "Incompatible", username = "david",
            bio = "Chef y yogui. Busco aprender fotografía y diseño.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.0, totalSessions = 3, totalMatches = 1,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0014",
            roleList = setOf(userRole),
        ))

        // TU5 — Elena (76.7% con TU1 — skill parcial + días solapan)
        // Ofrece: Inglés | Busca: Guitarra | Avail: Lun/Mié/Vie
        // Score: skillScore=(1+1)/3=0.667, availScore=3/3=1.0 → 76.7%
        val elenaUser = userRepository.save(User(
            email = "elena@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "Elena", lastName = "Parcial", username = "elena",
            bio = "Inglés nativo. Quiero aprender guitarra.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.3, totalSessions = 6, totalMatches = 2,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0015",
            roleList = setOf(userRole),
        ))

        // TU6 — Felipe (0% con TU1 — SIN skills, SIN días)
        // Ofrece: Fotografía | Busca: Cocina | Avail: Mar/Jue/Sáb
        // Score: skillScore=0/3=0.0, availScore=0/6=0.0 → 0% → NO aparece en sugerencias
        val felipeUser = userRepository.save(User(
            email = "felipe@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "Felipe", lastName = "Cero", username = "felipe",
            bio = "Fotógrafo profesional. Aprendo cocina.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 3.8, totalSessions = 2, totalMatches = 1,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0016",
            roleList = setOf(userRole),
        ))

        // TU7 — Gabriela (55% con TU1 — skills parcial + 2/3 días solapan)
        // Ofrece: Inglés + Cocina | Busca: Guitarra + Fotografía | Avail: Lun/Mié
        // Score: skillScore=(1+1)/4=0.5, availScore=2/3=0.667 → 55%
        val gabrielaUser = userRepository.save(User(
            email = "gabriela@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "Gabriela", lastName = "Borde", username = "gabriela",
            bio = "Inglés y cocina. Quiero aprender guitarra y fotografía.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.1, totalSessions = 4, totalMatches = 2,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0017",
            roleList = setOf(userRole),
        ))

        // TU8 — Hugo (para probar calificaciones y cascada de sesiones)
        // Ofrece: Piano | Busca: Inglés | Avail: Lun/Mié/Vie
        val hugoUser = userRepository.save(User(
            email = "hugo@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "Hugo", lastName = "Revisor", username = "hugo",
            bio = "Pianista clásico. Quiero aprender inglés.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 0.0, totalSessions = 1, totalMatches = 1,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0018",
            roleList = setOf(userRole),
        ))

        // ── Usuarios de fondo (para search, variedad) ───────────────────
        val mariaUser = userRepository.save(User(
            email = "maria@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "María", lastName = "López", username = "maria",
            bio = "Pianista y aficionada a la cocina. Busco aprender fotografía.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.7, totalSessions = 15, totalMatches = 4,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0003", roleList = setOf(userRole),
        ))
        val juanUser = userRepository.save(User(
            email = "juan@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "Juan", lastName = "Pérez", username = "juan",
            bio = "Diseñador gráfico. Quiero aprender piano.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.2, totalSessions = 6, totalMatches = 2,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0004", roleList = setOf(userRole),
        ))
        val anaUser = userRepository.save(User(
            email = "ana@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "Ana", lastName = "Ramírez", username = "ana",
            bio = "Instructora de yoga. Interesada en aprender piano.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.9, totalSessions = 20, totalMatches = 7,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0005", roleList = setOf(userRole),
        ))
        val sofiaUser = userRepository.save(User(
            email = "sofia@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "Sofía", lastName = "Torres", username = "sofia",
            bio = "Traductora bilingüe. Me interesa aprender guitarra.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.4, totalSessions = 7, totalMatches = 3,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0008", roleList = setOf(userRole),
        ))
        val pedroUser = userRepository.save(User(
            email = "pedro@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "Pedro", lastName = "Alvarado", username = "pedro",
            bio = "Profesor de idiomas. Quiero aprender guitarra.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.7, totalSessions = 14, totalMatches = 5,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0010", roleList = setOf(userRole),
        ))
        val danielUser = userRepository.save(User(
            email = "daniel@dexter.app", password = passwordEncoder.encode("user123"),
            firstName = "Daniel", lastName = "Rojas", username = "daniel",
            bio = "Bilingüe francés-español. Siempre quise aprender piano.",
            country = "CR", enabled = true, tokenExpired = false,
            rating = 4.6, totalSessions = 11, totalMatches = 4,
            createDate = Date(), updatedAt = Date(), phone = "+506 8888-0012", roleList = setOf(userRole),
        ))

        // ── Skills ──────────────────────────────────────────────────────
        skillRepository.saveAll(listOf(
            // TU1 — Ana: Ofrece Guitarra+Piano, Busca Inglés+Francés
            Skill(user = adminUser, name = "Guitarra", category = "Música",  skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = adminUser, name = "Piano",    category = "Música",  skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = adminUser, name = "Inglés",   category = "Idiomas", skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = adminUser, name = "Francés",  category = "Idiomas", skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            // TU2 — Bruno: Ofrece Inglés+Francés, Busca Guitarra+Piano → 100% con TU1
            Skill(user = brunoUser, name = "Inglés",   category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = brunoUser, name = "Francés",  category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = brunoUser, name = "Guitarra", category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = brunoUser, name = "Piano",    category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            // TU3 — Carla: Ofrece Inglés+Francés, Busca Guitarra+Piano → 70% (sin días)
            Skill(user = carlaUser, name = "Inglés",   category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = carlaUser, name = "Francés",  category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = carlaUser, name = "Guitarra", category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = carlaUser, name = "Piano",    category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            // TU4 — David: Ofrece Cocina+Yoga, Busca Fotografía+Diseño → 30% (sin skills match)
            Skill(user = davidUser, name = "Cocina",      category = "Gastronomía", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = davidUser, name = "Yoga",        category = "Bienestar",   skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = davidUser, name = "Fotografía",  category = "Arte",        skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = davidUser, name = "Diseño",      category = "Diseño",      skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            // TU5 — Elena: Ofrece Inglés, Busca Guitarra → 76.7% con TU1
            Skill(user = elenaUser, name = "Inglés",   category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = elenaUser, name = "Guitarra", category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            // TU6 — Felipe: Ofrece Fotografía, Busca Cocina → 0% con TU1
            Skill(user = felipeUser, name = "Fotografía", category = "Arte",        skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = felipeUser, name = "Cocina",     category = "Gastronomía", skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            // TU7 — Gabriela: Ofrece Inglés+Cocina, Busca Guitarra+Fotografía → 55% con TU1
            Skill(user = gabrielaUser, name = "Inglés",     category = "Idiomas",     skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = gabrielaUser, name = "Cocina",     category = "Gastronomía", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = gabrielaUser, name = "Guitarra",   category = "Música",      skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = gabrielaUser, name = "Fotografía", category = "Arte",        skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            // TU8 — Hugo: Ofrece Piano, Busca Inglés (para probar calificaciones)
            Skill(user = hugoUser, name = "Piano",  category = "Música",  skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = hugoUser, name = "Inglés", category = "Idiomas", skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            // Usuarios de fondo
            Skill(user = mariaUser, name = "Piano",      category = "Música",      skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = mariaUser, name = "Cocina",     category = "Gastronomía", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = mariaUser, name = "Fotografía", category = "Arte",        skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = mariaUser, name = "Guitarra",   category = "Música",      skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            Skill(user = juanUser, name = "Diseño Gráfico", category = "Diseño", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = juanUser, name = "Photoshop",      category = "Diseño", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = juanUser, name = "Piano",          category = "Música", skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
            Skill(user = juanUser, name = "Guitarra",       category = "Música", skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            Skill(user = anaUser, name = "Yoga",       category = "Bienestar", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = anaUser, name = "Meditación", category = "Bienestar", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = anaUser, name = "Piano",      category = "Música",   skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            Skill(user = sofiaUser, name = "Inglés",   category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = sofiaUser, name = "Francés",  category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = sofiaUser, name = "Guitarra", category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            Skill(user = pedroUser, name = "Inglés",   category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = pedroUser, name = "Francés",  category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = pedroUser, name = "Guitarra", category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),

            Skill(user = danielUser, name = "Francés", category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.ADVANCED),
            Skill(user = danielUser, name = "Inglés",  category = "Idiomas", skillRole = SkillRole.OFFERED, skillLevel = SkillLevel.INTERMEDIATE),
            Skill(user = danielUser, name = "Piano",   category = "Música",  skillRole = SkillRole.SOUGHT,  skillLevel = SkillLevel.BEGINNER),
        ))

        // ── Availability (ISO-8601: 1=Lun, 2=Mar, 3=Mié, 4=Jue, 5=Vie, 6=Sáb, 7=Dom) ──
        availabilityRepository.saveAll(listOf(
            // TU1 — Ana: Lun/Mié/Vie
            Availability(user = adminUser, dayOfWeek = 1, startTime = LocalTime.of(18, 0), endTime = LocalTime.of(20, 0)),
            Availability(user = adminUser, dayOfWeek = 3, startTime = LocalTime.of(18, 0), endTime = LocalTime.of(20, 0)),
            Availability(user = adminUser, dayOfWeek = 5, startTime = LocalTime.of(16, 0), endTime = LocalTime.of(18, 0)),

            // TU2 — Bruno: Lun/Mié/Vie → solapamiento 3/3 con TU1 → availScore=1.0
            Availability(user = brunoUser, dayOfWeek = 1, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),
            Availability(user = brunoUser, dayOfWeek = 3, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),
            Availability(user = brunoUser, dayOfWeek = 5, startTime = LocalTime.of(17, 0), endTime = LocalTime.of(19, 0)),

            // TU3 — Carla: Mar/Sáb → SIN solapamiento con TU1 → availScore=0 → score=70%
            Availability(user = carlaUser, dayOfWeek = 2, startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0)),
            Availability(user = carlaUser, dayOfWeek = 6, startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0)),

            // TU4 — David: Lun/Mié/Vie → solapamiento perfecto con TU1 (pero skills=0)
            Availability(user = davidUser, dayOfWeek = 1, startTime = LocalTime.of(17, 0), endTime = LocalTime.of(19, 0)),
            Availability(user = davidUser, dayOfWeek = 3, startTime = LocalTime.of(17, 0), endTime = LocalTime.of(19, 0)),
            Availability(user = davidUser, dayOfWeek = 5, startTime = LocalTime.of(15, 0), endTime = LocalTime.of(17, 0)),

            // TU5 — Elena: Lun/Mié/Vie → solapamiento 3/3 con TU1 → availScore=1.0
            Availability(user = elenaUser, dayOfWeek = 1, startTime = LocalTime.of(18, 0), endTime = LocalTime.of(20, 0)),
            Availability(user = elenaUser, dayOfWeek = 3, startTime = LocalTime.of(18, 0), endTime = LocalTime.of(20, 0)),
            Availability(user = elenaUser, dayOfWeek = 5, startTime = LocalTime.of(16, 0), endTime = LocalTime.of(18, 0)),

            // TU6 — Felipe: Mar/Jue/Sáb → SIN solapamiento con TU1 → availScore=0 → score=0%
            Availability(user = felipeUser, dayOfWeek = 2, startTime = LocalTime.of(9, 0), endTime = LocalTime.of(11, 0)),
            Availability(user = felipeUser, dayOfWeek = 4, startTime = LocalTime.of(9, 0), endTime = LocalTime.of(11, 0)),
            Availability(user = felipeUser, dayOfWeek = 6, startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0)),

            // TU7 — Gabriela: Lun/Mié → overlap=2, union={1,3,5}=3 → availScore=2/3=0.667
            Availability(user = gabrielaUser, dayOfWeek = 1, startTime = LocalTime.of(17, 0), endTime = LocalTime.of(19, 0)),
            Availability(user = gabrielaUser, dayOfWeek = 3, startTime = LocalTime.of(17, 0), endTime = LocalTime.of(19, 0)),

            // TU8 — Hugo: Lun/Mié/Vie (para match con Ana que usa esos días)
            Availability(user = hugoUser, dayOfWeek = 1, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),
            Availability(user = hugoUser, dayOfWeek = 3, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),
            Availability(user = hugoUser, dayOfWeek = 5, startTime = LocalTime.of(17, 0), endTime = LocalTime.of(19, 0)),

            // Usuarios de fondo
            Availability(user = mariaUser, dayOfWeek = 1, startTime = LocalTime.of(9, 0),  endTime = LocalTime.of(11, 0)),
            Availability(user = mariaUser, dayOfWeek = 4, startTime = LocalTime.of(15, 0), endTime = LocalTime.of(17, 0)),
            Availability(user = mariaUser, dayOfWeek = 6, startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0)),

            Availability(user = juanUser, dayOfWeek = 1, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),
            Availability(user = juanUser, dayOfWeek = 3, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),

            Availability(user = anaUser, dayOfWeek = 1, startTime = LocalTime.of(7, 0),  endTime = LocalTime.of(9, 0)),
            Availability(user = anaUser, dayOfWeek = 3, startTime = LocalTime.of(7, 0),  endTime = LocalTime.of(9, 0)),
            Availability(user = anaUser, dayOfWeek = 6, startTime = LocalTime.of(9, 0),  endTime = LocalTime.of(11, 0)),

            Availability(user = sofiaUser, dayOfWeek = 2, startTime = LocalTime.of(9, 0), endTime = LocalTime.of(11, 0)),
            Availability(user = sofiaUser, dayOfWeek = 4, startTime = LocalTime.of(9, 0), endTime = LocalTime.of(11, 0)),
            Availability(user = sofiaUser, dayOfWeek = 6, startTime = LocalTime.of(10, 0), endTime = LocalTime.of(12, 0)),

            Availability(user = pedroUser, dayOfWeek = 1, startTime = LocalTime.of(18, 0), endTime = LocalTime.of(20, 0)),
            Availability(user = pedroUser, dayOfWeek = 3, startTime = LocalTime.of(18, 0), endTime = LocalTime.of(20, 0)),

            Availability(user = danielUser, dayOfWeek = 1, startTime = LocalTime.of(19, 0), endTime = LocalTime.of(21, 0)),
            Availability(user = danielUser, dayOfWeek = 5, startTime = LocalTime.of(16, 0), endTime = LocalTime.of(18, 0)),
        ))

        // ── Matches pre-armados ─────────────────────────────────────────

        // M1: Bruno → Ana (PENDING — para probar solicitudes recibidas y aceptar con picker)
        matchRepository.save(Match(
            initiator = brunoUser, receiver = adminUser,
            compatibilityScore = 100.0, status = MatchStatus.PENDING, createdAt = Date(),
        ))

        // M2: Carla → Ana (PENDING — para probar rechazo de solicitud)
        matchRepository.save(Match(
            initiator = carlaUser, receiver = adminUser,
            compatibilityScore = 70.0, status = MatchStatus.PENDING, createdAt = Date(),
        ))

        // M3: Ana ↔ Elena (ACCEPTED — 6 sesiones alternadas, 0 completadas)
        // Ana enseña Guitarra (par), Elena enseña Inglés (impar)
        val matchElena = matchRepository.save(Match(
            initiator = adminUser, receiver = elenaUser,
            compatibilityScore = 76.7, status = MatchStatus.ACCEPTED,
            initiatorSkill = "Guitarra", receiverSkill = "Inglés",
            createdAt = Date(),
        ))

        val guitarraAna = skillRepository.findByUserId(adminUser.id!!)
            .first { it.skillRole == SkillRole.OFFERED && it.name == "Guitarra" }
        val inglesElena = skillRepository.findByUserId(elenaUser.id!!)
            .first { it.skillRole == SkillRole.OFFERED && it.name == "Inglés" }

        // Fechas sugeridas: próximos Lun/Mié/Vie (en común Ana y Elena)
        val today = java.util.Calendar.getInstance()
        fun nextWeekday(dayOfWeek: Int): Date {
            val cal = java.util.Calendar.getInstance()
            while (cal.get(java.util.Calendar.DAY_OF_WEEK) != dayOfWeek) cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
            cal.set(java.util.Calendar.HOUR_OF_DAY, 18)
            cal.set(java.util.Calendar.MINUTE, 0)
            return cal.time
        }
        val lun1 = nextWeekday(java.util.Calendar.MONDAY)
        val mie1 = nextWeekday(java.util.Calendar.WEDNESDAY)
        val vie1 = nextWeekday(java.util.Calendar.FRIDAY)
        val cal2 = java.util.Calendar.getInstance().also { it.time = lun1; it.add(java.util.Calendar.WEEK_OF_YEAR, 1) }
        val lun2 = cal2.time
        val cal3 = java.util.Calendar.getInstance().also { it.time = mie1; it.add(java.util.Calendar.WEEK_OF_YEAR, 1) }
        val mie2 = cal3.time
        val cal4 = java.util.Calendar.getInstance().also { it.time = vie1; it.add(java.util.Calendar.WEEK_OF_YEAR, 1) }
        val vie2 = cal4.time

        sessionRepository.saveAll(listOf(
            // Impares (1,3,5): Elena enseña Inglés → receiver enseña
            Session(match = matchElena, skill = inglesElena, sessionNumber = 1,
                title = "Primeros pasos en Inglés: Saludos y presentaciones",
                description = "Aprende los saludos básicos y cómo presentarte en inglés.",
                status = SessionStatus.SCHEDULED, scheduledAt = lun1, createdAt = Date()),
            // Pares (2,4,6): Ana enseña Guitarra → initiator enseña
            Session(match = matchElena, skill = guitarraAna, sessionNumber = 2,
                title = "Guitarra: Los 3 acordes fundamentales",
                description = "Em, Am y C — los acordes con los que puedes tocar cientos de canciones.",
                status = SessionStatus.SCHEDULED, scheduledAt = mie1, createdAt = Date()),
            Session(match = matchElena, skill = inglesElena, sessionNumber = 3,
                title = "Inglés conversacional: Situaciones cotidianas",
                description = "Practicamos diálogos de la vida diaria: tienda, restaurante, trabajo.",
                status = SessionStatus.SCHEDULED, scheduledAt = vie1, createdAt = Date()),
            Session(match = matchElena, skill = guitarraAna, sessionNumber = 4,
                title = "Guitarra: Cambios de acorde y ritmo básico",
                description = "Aprende a cambiar entre acordes sin perder el pulso.",
                status = SessionStatus.SCHEDULED, scheduledAt = lun2, createdAt = Date()),
            Session(match = matchElena, skill = inglesElena, sessionNumber = 5,
                title = "Inglés: Verbos irregulares más comunes",
                description = "Dominamos los 20 verbos irregulares que más se usan.",
                status = SessionStatus.SCHEDULED, scheduledAt = mie2, createdAt = Date()),
            Session(match = matchElena, skill = guitarraAna, sessionNumber = 6,
                title = "Guitarra: Primera canción completa",
                description = "Tocamos una canción completa de principio a fin.",
                status = SessionStatus.SCHEDULED, scheduledAt = vie2, createdAt = Date()),
        ))

        // M4: Ana ↔ Hugo (ACCEPTED — 6 sesiones, sesión 1 COMPLETED + review de Hugo)
        // Ana enseña Piano (par), Hugo enseña Inglés (impar)
        val matchHugo = matchRepository.save(Match(
            initiator = adminUser, receiver = hugoUser,
            compatibilityScore = 76.7, status = MatchStatus.ACCEPTED,
            initiatorSkill = "Piano", receiverSkill = "Inglés",
            createdAt = Date(),
        ))

        val pianoAna = skillRepository.findByUserId(adminUser.id!!)
            .first { it.skillRole == SkillRole.OFFERED && it.name == "Piano" }
        val inglesHugo = skillRepository.findByUserId(hugoUser.id!!)
            .first { it.skillRole == SkillRole.OFFERED && it.name == "Inglés" }

        val session1Hugo = sessionRepository.save(
            Session(match = matchHugo, skill = inglesHugo, sessionNumber = 1,
                title = "Inglés: Fundamentos del idioma",
                description = "Pronunciación, alfabeto y frases esenciales.",
                status = SessionStatus.COMPLETED,
                scheduledAt = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L),
                completedAt = Date(System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000L),
                createdAt = Date(System.currentTimeMillis() - 8 * 24 * 60 * 60 * 1000L))
        )

        sessionRepository.saveAll(listOf(
            Session(match = matchHugo, skill = pianoAna, sessionNumber = 2,
                title = "Piano: Postura y notas básicas",
                description = "Aprendemos la postura correcta y las notas en el teclado.",
                status = SessionStatus.SCHEDULED, scheduledAt = lun1, createdAt = Date()),
            Session(match = matchHugo, skill = inglesHugo, sessionNumber = 3,
                title = "Inglés: Presente simple y vocabulario",
                description = "Conjugación del presente simple con vocabulario cotidiano.",
                status = SessionStatus.SCHEDULED, scheduledAt = mie1, createdAt = Date()),
            Session(match = matchHugo, skill = pianoAna, sessionNumber = 4,
                title = "Piano: Do mayor y primeras melodías",
                description = "Escala de Do mayor y melodías simples con la mano derecha.",
                status = SessionStatus.SCHEDULED, scheduledAt = vie1, createdAt = Date()),
            Session(match = matchHugo, skill = inglesHugo, sessionNumber = 5,
                title = "Inglés: Pasado simple y narración",
                description = "Contamos historias usando el pasado simple.",
                status = SessionStatus.SCHEDULED, scheduledAt = lun2, createdAt = Date()),
            Session(match = matchHugo, skill = pianoAna, sessionNumber = 6,
                title = "Piano: Ambas manos — primera pieza completa",
                description = "Coordinamos ambas manos para tocar una pieza sencilla.",
                status = SessionStatus.SCHEDULED, scheduledAt = mie2, createdAt = Date()),
        ))

        // Actualizar totalSessions de los participantes del match Hugo (sesión 1 completada)
        adminUser.totalSessions = (adminUser.totalSessions ?: 0) + 1
        hugoUser.totalSessions = (hugoUser.totalSessions ?: 0) + 1
        userRepository.save(adminUser)
        userRepository.save(hugoUser)

        // Review: Ana califica a Hugo con 5 estrellas (primera review de Hugo)
        reviewRepository.save(Review(
            reviewer = adminUser, reviewee = hugoUser, session = session1Hugo,
            rating = 5,
            comment = "Excelente primera clase de inglés, muy dinámico y claro.",
            createdAt = Date(),
        ))
        // Actualizar rating de Hugo: primera review = 5.0
        hugoUser.rating = 5.0
        userRepository.save(hugoUser)

        println(
            "=== SEED COMPLETADO ===" +
            "\n${userRepository.count()} usuarios (TU1-TU8 + TU9 admin + 6 fondo)" +
            "\n${skillRepository.count()} skills" +
            "\n${availabilityRepository.count()} availability entries" +
            "\n${matchRepository.count()} matches (2 PENDING, 2 ACCEPTED)" +
            "\n${sessionRepository.count()} sessions (6+6=12 total, 1 COMPLETED)" +
            "\n${reviewRepository.count()} reviews" +
            "\n\nScores esperados desde admin@dexter.app:" +
            "\n  Bruno   (user@dexter.app)     = 100.0%" +
            "\n  Elena   (elena@dexter.app)    =  76.7%" +
            "\n  Carla   (carla@dexter.app)    =  70.0%" +
            "\n  Gabriela(gabriela@dexter.app) =  55.0%" +
            "\n  David   (david@dexter.app)    =  30.0%  ← NO sugerencia" +
            "\n  Felipe  (felipe@dexter.app)   =   0.0%  ← NO sugerencia"
        )
    }
}
