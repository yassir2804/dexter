package com.dexter.app.data.datasource

import com.dexter.app.domain.model.Session
import com.dexter.app.domain.model.SessionStatus
import java.util.Calendar
import java.util.Date

/**
 * In-memory provider for sample Session data used by the repository during development.
 *
 * Generates a 6-session roadmap per match (2 completed, 1 scheduled as next,
 * 3 pending) mirroring the PlanScreen prototype. Replaced by the Mockoon API
 * once Yazir wires it up.
 */
class SessionProvider {
    companion object {

        private fun daysFromNow(days: Int): Date {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, days)
            return cal.time
        }

        private val roadmapBase = listOf(
            Triple(1, "Fundamentos básicos", "Acordes abiertos y postura"),
            Triple(2, "Rítmica y compases", "Patrones de rasgueo en 4/4"),
            Triple(3, "Escalas mayores", "Escala de Do y Sol en 2 octavas"),
            Triple(4, "Canciones populares", "Aplicación práctica con canción"),
            Triple(5, "Técnicas avanzadas", "Técnicas aplicadas en contexto real"),
            Triple(6, "Improvisación", "Licks básicos sobre progresiones")
        )

        fun findSessionsByMatch(matchId: Long): List<Session> {
            val baseId = matchId * 10
            val skillId = matchId
            return roadmapBase.mapIndexed { index, (number, title, desc) ->
                val status = when (index) {
                    0, 1 -> SessionStatus.COMPLETED
                    2 -> SessionStatus.SCHEDULED
                    else -> SessionStatus.SCHEDULED
                }
                val scheduled = when (index) {
                    0 -> daysFromNow(-14)
                    1 -> daysFromNow(-7)
                    2 -> daysFromNow(2)
                    3 -> daysFromNow(9)
                    4 -> daysFromNow(16)
                    else -> daysFromNow(23)
                }
                val completed = when (index) {
                    0 -> daysFromNow(-14)
                    1 -> daysFromNow(-7)
                    else -> null
                }
                Session(
                    id = baseId + number,
                    matchId = matchId,
                    skillId = skillId,
                    sessionNumber = number,
                    title = title,
                    description = desc,
                    status = status,
                    scheduledAt = scheduled,
                    completedAt = completed,
                    createdAt = daysFromNow(-30)
                )
            }
        }

        fun findSessionById(sessionId: Long): Session? =
            findSessionsByMatch(matchId = sessionId / 10).firstOrNull { it.id == sessionId }

        fun findActiveSession(sessionId: Long): Session {
            val base = findSessionById(sessionId) ?: findSessionsByMatch(1L)[2]
            return base.copy(
                status = SessionStatus.SCHEDULED,
                scheduledAt = Date()
            )
        }
    }
}
