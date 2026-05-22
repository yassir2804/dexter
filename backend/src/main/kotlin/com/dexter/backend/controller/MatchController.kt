package com.dexter.backend.controller

import com.dexter.backend.dto.MatchDetailResult
import com.dexter.backend.dto.MatchResult
import com.dexter.backend.dto.MatchInput
import com.dexter.backend.dto.MatchSummaryResult
import com.dexter.backend.service.MatchService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${url.matches}")
class MatchController(private val matchService: MatchService) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findByUser(@RequestParam userId: Long): List<MatchSummaryResult> =
        matchService.findByUser(userId)

    @GetMapping("/{matchId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable matchId: Long): MatchResult =
        matchService.findById(matchId)

    @GetMapping("/{matchId}/detail", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findDetail(
        @PathVariable matchId: Long,
        @RequestParam viewerUserId: Long,
    ): MatchDetailResult = matchService.findDetail(matchId, viewerUserId)

    @GetMapping("/suggestions", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findSuggestions(@RequestParam userId: Long): List<MatchSummaryResult> =
        matchService.findSuggestions(userId)

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody request: MatchInput): MatchSummaryResult =
        matchService.create(request)

    @PatchMapping("/{id}/accept", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun accept(
        @PathVariable id: Long,
        @RequestParam viewerUserId: Long,
        @RequestParam(required = false) initiatorSkill: String? = null,
    ): MatchSummaryResult = matchService.accept(id, viewerUserId, initiatorSkill)

    @PatchMapping("/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun reject(@PathVariable id: Long) = matchService.reject(id)

    @PostMapping("/{id}/ensure-sessions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun ensureSessions(@PathVariable id: Long) = matchService.ensureSessions(id)
}
