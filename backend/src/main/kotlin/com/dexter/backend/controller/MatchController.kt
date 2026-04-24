package com.dexter.backend.controller

import com.dexter.backend.dto.MatchDto
import com.dexter.backend.dto.MatchSummaryDto
import com.dexter.backend.entity.MatchStatus
import com.dexter.backend.service.MatchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Expone los endpoints de matches en dos paths:
 *  - ${url.matches} → GET con filtros por user/status/id
 *  - ${url.matchSummaries} → GET proyección compacta para Home
 *
 * No usa @RequestMapping a nivel de clase para no forzar un prefijo común.
 */
@RestController
class MatchController(private val matchService: MatchService) {

    @GetMapping("\${url.matches}")
    fun findByUser(
        @RequestParam userId: Long,
        @RequestParam(required = false) status: MatchStatus?,
    ): List<MatchDto> = if (status != null)
        matchService.findByUserIdAndStatus(userId, status)
    else
        matchService.findByUserId(userId)

    @GetMapping("\${url.matches}/{matchId}")
    fun findById(@PathVariable matchId: Long): MatchDto =
        matchService.findById(matchId)

    @GetMapping("\${url.matchSummaries}")
    fun findSummariesByUser(@RequestParam userId: Long): List<MatchSummaryDto> =
        matchService.findSummariesByUserId(userId)
}
