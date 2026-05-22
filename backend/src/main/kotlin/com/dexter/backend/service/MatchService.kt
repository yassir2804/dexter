package com.dexter.backend.service

import com.dexter.backend.dto.MatchDetailResult
import com.dexter.backend.dto.MatchResult
import com.dexter.backend.dto.MatchInput
import com.dexter.backend.dto.MatchSummaryResult

interface MatchService {
    fun findByUser(userId: Long): List<MatchSummaryResult>
    fun findById(matchId: Long): MatchResult
    fun findDetail(matchId: Long, viewerUserId: Long): MatchDetailResult
    fun findSuggestions(userId: Long): List<MatchSummaryResult>
    fun create(request: MatchInput): MatchSummaryResult
    fun accept(id: Long, viewerUserId: Long, initiatorSkill: String? = null): MatchSummaryResult
    fun reject(id: Long)
    fun ensureSessions(matchId: Long)
}
