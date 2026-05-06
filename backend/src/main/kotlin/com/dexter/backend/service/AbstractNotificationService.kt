package com.dexter.backend.service

import com.dexter.backend.dto.NotificationDetails
import com.dexter.backend.dto.NotificationRequest
import com.dexter.backend.entity.Notification
import com.dexter.backend.entity.NotificationType
import com.dexter.backend.repository.NotificationRepository
import com.dexter.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AbstractNotificationService(
    @Autowired val notificationRepository: NotificationRepository,
    @Autowired val userRepository: UserRepository,
) : NotificationService {

    override fun findByUser(userId: Long): List<NotificationDetails> =
        notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).map(::toDetails)

    override fun findUnreadByUser(userId: Long): List<NotificationDetails> =
        notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false).map(::toDetails)

    override fun findById(id: Long): NotificationDetails =
        toDetails(notificationRepository.findById(id).orElseThrow {
            NoSuchElementException("Notification with id $id not found")
        })

    override fun create(request: NotificationRequest): NotificationDetails {
        val userId = request.userId ?: throw IllegalArgumentException("userId is required")
        val title = request.title ?: throw IllegalArgumentException("title is required")
        val typeStr = request.notificationType
            ?: throw IllegalArgumentException("notificationType is required")
        val type = try {
            NotificationType.valueOf(typeStr)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid notificationType: $typeStr")
        }

        val user = userRepository.findById(userId).orElseThrow {
            NoSuchElementException("User with id $userId not found")
        }

        val saved = notificationRepository.save(
            Notification(
                user = user,
                referenceId = request.referenceId,
                title = title,
                body = request.body,
                notificationType = type,
                isRead = false,
                createdAt = Date(),
            )
        )
        return toDetails(saved)
    }

    override fun markRead(id: Long) {
        val n = notificationRepository.findById(id).orElseThrow {
            NoSuchElementException("Notification with id $id not found")
        }
        n.isRead = true
        notificationRepository.save(n)
    }

    override fun delete(id: Long) {
        if (!notificationRepository.existsById(id)) {
            throw NoSuchElementException("Notification with id $id not found")
        }
        notificationRepository.deleteById(id)
    }

    private fun toDetails(n: Notification): NotificationDetails = NotificationDetails(
        id = n.id,
        userId = n.user?.id,
        referenceId = n.referenceId,
        title = n.title,
        body = n.body,
        notificationType = n.notificationType.name,
        isRead = n.isRead,
        createdAt = n.createdAt?.time,
    )
}
