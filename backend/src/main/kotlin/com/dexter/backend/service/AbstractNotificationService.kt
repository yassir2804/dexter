package com.dexter.backend.service

import com.dexter.backend.dto.NotificationDetails
import com.dexter.backend.mapper.NotificationMapper
import com.dexter.backend.repository.NotificationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AbstractNotificationService(
    @Autowired val notificationRepository: NotificationRepository,
    @Autowired val notificationMapper: NotificationMapper,
) : NotificationService {

    override fun findByUser(userId: Long): List<NotificationDetails> =
        notificationMapper.toDetailsList(
            notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
        )

    override fun markRead(id: Long) {
        val notification = notificationRepository.findById(id)
            .orElseThrow { NoSuchElementException("Notification $id not found") }
        notification.isRead = true
        notificationRepository.save(notification)
    }
}
