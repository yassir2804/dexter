package com.dexter.backend.service

import com.dexter.backend.dto.MatchDto
import com.dexter.backend.dto.MatchSummaryDto
import com.dexter.backend.entity.MatchStatus

interface MatchService {

    /**
     * Lista todos los matches donde el user es initiator O receiver.
     */
    fun findByUserId(userId: Long): List<MatchDto>

    /**
     * Lista matches del user filtrados por estado (PENDING | ACCEPTED | ...).
     */
    fun findByUserIdAndStatus(userId: Long, status: MatchStatus): List<MatchDto>

    /**
     * Un match por id.
     * @throws NoSuchElementException si no existe → 404.
     */
    fun findById(matchId: Long): MatchDto

    /**
     * Proyección compacta de todos los matches del user (Home cards).
     */
    fun findSummariesByUserId(userId: Long): List<MatchSummaryDto>
}
