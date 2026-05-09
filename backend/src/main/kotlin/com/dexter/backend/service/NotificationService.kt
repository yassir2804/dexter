package com.dexter.backend.service

import com.dexter.backend.dto.NotificationDetails

interface NotificationService {
    fun findByUser(userId: Long): List<NotificationDetails>
    fun markRead(id: Long)
}
