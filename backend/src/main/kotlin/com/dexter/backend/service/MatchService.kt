package com.dexter.backend.service

import com.dexter.backend.dto.MatchResult
import com.dexter.backend.dto.MatchInput
import com.dexter.backend.dto.MatchSummaryResult

interface MatchService {
    fun findByUser(userId: Long): List<MatchSummaryResult>
    fun findById(matchId: Long): MatchResult
    fun create(request: MatchInput): MatchSummaryResult
    fun accept(id: Long, viewerUserId: Long): MatchSummaryResult
    fun reject(id: Long)
}
