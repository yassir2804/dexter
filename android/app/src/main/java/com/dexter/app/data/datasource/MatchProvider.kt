package com.dexter.app.data.datasource



import com.dexter.app.domain.model.Match
import com.dexter.app.domain.model.MatchDetail
import com.dexter.app.domain.model.MatchStatus
import com.dexter.app.domain.model.MatchSummary
import java.util.Date

/**
 * In-memory provider for sample Match data used by the repository during development.
 */
class MatchProvider {
    companion object {
        private val sampleMatches = listOf(
            Match(
                id = 1,
                initiatorId = 2,
                receiverId = 1,
                compatibilityScore = 0.86,
                status = MatchStatus.PENDING,
                createdAt = Date()
            ),
            Match(
                id = 2,
                initiatorId = 1,
                receiverId = 3,
                compatibilityScore = 0.74,
                status = MatchStatus.ACCEPTED,
                createdAt = Date()
            ),
            Match(
                id = 3,
                initiatorId = 4,
                receiverId = 1,
                compatibilityScore = 0.69,
                status = MatchStatus.PENDING,
                createdAt = Date()
            ),
            Match(
                id = 4,
                initiatorId = 5,
                receiverId = 1,
                compatibilityScore = 0.92,
                status = MatchStatus.ACCEPTED,
                createdAt = Date()
            ),
            Match(
                id = 5,
                initiatorId = 1,
                receiverId = 6,
                compatibilityScore = 0.55,
                status = MatchStatus.REJECTED,
                createdAt = Date()
            ),
            Match(
                id = 6,
                initiatorId = 7,
                receiverId = 1,
                compatibilityScore = 0.78,
                status = MatchStatus.PENDING,
                createdAt = Date()
            ),
            Match(
                id = 7,
                initiatorId = 8,
                receiverId = 9,
                compatibilityScore = 0.63,
                status = MatchStatus.CANCELLED,
                createdAt = Date()
            ),
            Match(
                id = 8,
                initiatorId = 3,
                receiverId = 1,
                compatibilityScore = 0.81,
                status = MatchStatus.ACCEPTED,
                createdAt = Date()
            ),
            Match(
                id = 9,
                initiatorId = 10,
                receiverId = 1,
                compatibilityScore = 0.47,
                status = MatchStatus.PENDING,
                createdAt = Date()
            )
        )

        fun findMatchesForUser(userId: Long): List<Match> = sampleMatches.filter {
            it.initiatorId == userId || it.receiverId == userId
        }

        fun findAll(): List<Match> = sampleMatches

        private val sampleSummaries = listOf(
            MatchSummary(
                matchId = 1L, otherUserId = 2L,
                name = "Maria Lopez", initials = "ML", avatarUrl = null,
                skillsOffered = listOf("Guitarra"), skillsSought = listOf("Inglés"),
                rating = 4.8, totalSessions = 23, compatibilityScore = 95.0,
                status = "ACCEPTED", sessionsCount = 5
            ),
            MatchSummary(
                matchId = 2L, otherUserId = 3L,
                name = "Juan Perez", initials = "JP", avatarUrl = null,
                skillsOffered = listOf("Programación"), skillsSought = listOf("Diseño gráfico"),
                rating = 4.2, totalSessions = 15, compatibilityScore = 87.0,
                status = "ACCEPTED", sessionsCount = 5
            ),
            MatchSummary(
                matchId = 3L, otherUserId = 4L,
                name = "Ana Ramirez", initials = "AR", avatarUrl = null,
                skillsOffered = listOf("Fotografía"), skillsSought = listOf("Marketing"),
                rating = 4.9, totalSessions = 31, compatibilityScore = 82.0,
                status = "PENDING", sessionsCount = 0
            )
        )

        fun findMatchSummaries(userId: Long): List<MatchSummary> = sampleSummaries

        // Mock MatchDetail matching the React prototype (MatchDetailScreen.tsx).
        // Indexed by matchId for quick lookup. Falls back to the first one if
        // the requested id is not found (useful while the API is not wired yet).
        private val sampleDetails = listOf(
            MatchDetail(
                matchId = 1L,
                otherUserId = 2L,
                initials = "ML",
                name = "Maria Lopez",
                avatarUrl = null,
                bio = "Diseñadora gráfica apasionada por la música. Me encanta aprender cosas nuevas.",
                rating = 4.8,
                sessionsCompleted = 23,
                compatibilityPercent = 95,
                offeredSkills = listOf("Guitarra", "Piano"),
                soughtSkills = listOf("Inglés", "Redacción"),
                availability = listOf(1, 2, 4),
                aiAnalysis = "Tu horario coincide con el de Maria los lunes, martes y jueves. Su nivel avanzado en guitarra se alinea con tu objetivo de aprender en 3 meses.",
                status = MatchStatus.PENDING
            ),
            MatchDetail(
                matchId = 2L,
                otherUserId = 3L,
                initials = "JP",
                name = "Juan Perez",
                avatarUrl = null,
                bio = "Developer full-stack con interés en diseño. Busco mejorar mi ojo visual.",
                rating = 4.2,
                sessionsCompleted = 15,
                compatibilityPercent = 87,
                offeredSkills = listOf("Programación", "React"),
                soughtSkills = listOf("Diseño gráfico", "Figma"),
                availability = listOf(0, 2, 5),
                aiAnalysis = "Juan tiene experiencia en proyectos web similares. Es buen match por su disponibilidad en fines de semana.",
                status = MatchStatus.ACCEPTED
            ),
            MatchDetail(
                matchId = 3L,
                otherUserId = 4L,
                initials = "AR",
                name = "Ana Ramirez",
                avatarUrl = null,
                bio = "Fotógrafa profesional. Trabajo con bodas y retratos. Me interesa el marketing digital.",
                rating = 4.9,
                sessionsCompleted = 31,
                compatibilityPercent = 82,
                offeredSkills = listOf("Fotografía", "Lightroom"),
                soughtSkills = listOf("Marketing", "SEO"),
                availability = listOf(3, 4, 5, 6),
                aiAnalysis = "Ana tiene 5 estrellas en sus últimas 10 sesiones. Su metodología es muy visual y práctica.",
                status = MatchStatus.ACCEPTED
            )
        )

        fun findMatchDetailById(matchId: Long): MatchDetail =
            sampleDetails.firstOrNull { it.matchId == matchId } ?: sampleDetails.first()
    }
}
