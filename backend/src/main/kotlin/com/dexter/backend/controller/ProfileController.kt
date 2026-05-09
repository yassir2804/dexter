package com.dexter.backend.controller

import com.dexter.backend.dto.ProfileSummaryResult
import com.dexter.backend.service.ProfileService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${url.profile}")
class ProfileController(private val profileService: ProfileService) {

    @GetMapping
    fun findByUserId(@RequestParam userId: Long): ProfileSummaryResult =
        profileService.findByUserId(userId)

    @PutMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE],
    )
    fun update(
        @RequestParam userId: Long,
        @RequestBody dto: ProfileSummaryResult,
    ): ProfileSummaryResult =
        profileService.update(userId, dto)
}
