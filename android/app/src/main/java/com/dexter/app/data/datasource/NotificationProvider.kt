package com.dexter.app.data.datasource

import com.dexter.app.domain.model.Notification
import com.dexter.app.domain.model.NotificationType
import java.util.Calendar
import java.util.Date

/**
 * In-memory provider for sample Notification data during development.
 * Covers every [NotificationType] so the NotificationsScreen can render
 * all icon/color variants.
 */
class NotificationProvider {
    companion object {

        private fun minutesAgo(min: Int): Date {
            val cal = Calendar.getInstance()
            cal.add(Calendar.MINUTE, -min)
            return cal.time
        }

        private fun hoursAgo(hours: Int): Date {
            val cal = Calendar.getInstance()
            cal.add(Calendar.HOUR, -hours)
            return cal.time
        }

        private fun daysAgo(days: Int): Date {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -days)
            return cal.time
        }

        private val notifications = listOf(
            Notification(
                id = 1, userId = 1, referenceId = 1,
                title = "Nuevo match",
                body = "Maria Lopez quiere intercambiar habilidades contigo",
                notificationType = NotificationType.NEW_MATCH,
                isRead = false,
                createdAt = minutesAgo(15)
            ),
            Notification(
                id = 2, userId = 1, referenceId = 13,
                title = "Sesión próxima",
                body = "Tu clase de guitarra con Maria empieza en 1 hora",
                notificationType = NotificationType.SESSION_REMINDER,
                isRead = false,
                createdAt = hoursAgo(1)
            ),
            Notification(
                id = 3, userId = 1, referenceId = 2,
                title = "Sugerencia IA",
                body = "Encontramos 3 nuevos matches que pueden interesarte",
                notificationType = NotificationType.AI_SUGGESTION,
                isRead = false,
                createdAt = hoursAgo(3)
            ),
            Notification(
                id = 4, userId = 1, referenceId = 5,
                title = "Nueva reseña",
                body = "Juan Perez te dejó una reseña de 5 estrellas",
                notificationType = NotificationType.NEW_REVIEW,
                isRead = true,
                createdAt = daysAgo(1)
            ),
            Notification(
                id = 6, userId = 1, referenceId = 3,
                title = "Match aceptado",
                body = "Ana Ramirez aceptó tu solicitud de intercambio",
                notificationType = NotificationType.NEW_MATCH,
                isRead = true,
                createdAt = daysAgo(3)
            )
        )

        fun findForUser(userId: Long): List<Notification> =
            notifications.filter { it.userId == userId }
    }
}
