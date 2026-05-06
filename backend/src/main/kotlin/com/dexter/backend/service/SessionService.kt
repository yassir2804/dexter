package com.dexter.backend.service

import com.dexter.backend.dto.SessionDetails
import com.dexter.backend.dto.SessionRequest

interface SessionService {
    fun findByMatch(matchId: Long): List<SessionDetails>
    fun findById(id: Long): SessionDetails
    fun create(request: SessionRequest): SessionDetails
    fun update(id: Long, request: SessionRequest): SessionDetails
    fun delete(id: Long)
}
