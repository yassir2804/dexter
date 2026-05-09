package com.dexter.backend.service

import com.dexter.backend.dto.MatchDto
import com.dexter.backend.dto.MatchRequest
import com.dexter.backend.dto.MatchSummaryDto

interface MatchService {
    fun findByUser(userId: Long): List<MatchSummaryDto>
    fun findById(matchId: Long): MatchDto
    fun create(request: MatchRequest): MatchSummaryDto
    fun accept(id: Long, viewerUserId: Long): MatchSummaryDto
    fun reject(id: Long)
}
