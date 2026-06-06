package com.dexter.backend.service

import com.dexter.backend.client.AiScheduleSlot
import com.dexter.backend.dto.SessionDetails
import com.dexter.backend.dto.SessionInput

interface SessionService {
    fun findByMatch(matchId: Long): List<SessionDetails>
    fun findById(id: Long): SessionDetails
    fun create(request: SessionInput): SessionDetails
    fun update(id: Long, request: SessionInput): SessionDetails
    fun complete(id: Long): SessionDetails
    fun delete(id: Long)
    fun suggestSchedule(sessionId: Long): List<AiScheduleSlot>
}
