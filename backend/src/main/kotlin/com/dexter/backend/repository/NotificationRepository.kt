package com.dexter.backend.repository

import com.dexter.backend.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Notification>
    fun findByUserIdAndIsReadOrderByCreatedAtDesc(userId: Long, isRead: Boolean): List<Notification>
}
