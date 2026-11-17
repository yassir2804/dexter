package com.dexter.backend.service

import com.dexter.backend.dto.NotificationDetails
import com.dexter.backend.dto.NotificationInput

interface NotificationService {
    fun findByUser(userId: Long): List<NotificationDetails>
    fun findUnreadByUser(userId: Long): List<NotificationDetails>
    fun findById(id: Long): NotificationDetails
    fun create(request: NotificationInput): NotificationDetails
    fun markRead(id: Long)
    fun delete(id: Long)
}
