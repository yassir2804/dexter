package com.dexter.backend.controller

import com.dexter.backend.dto.NotificationDetails
import com.dexter.backend.dto.NotificationInput
import com.dexter.backend.service.NotificationService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
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
@RequestMapping("\${url.notifications}")
class NotificationController(
    private val notificationService: NotificationService,
) {

    @GetMapping
    fun findByUser(
        @RequestParam userId: Long,
        @RequestParam(required = false, defaultValue = "false") unread: Boolean,
    ): List<NotificationDetails> = if (unread)
        notificationService.findUnreadByUser(userId)
    else
        notificationService.findByUser(userId)

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): NotificationDetails = notificationService.findById(id)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun create(@RequestBody request: NotificationInput): NotificationDetails =
        notificationService.create(request)

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun markRead(@PathVariable id: Long) = notificationService.markRead(id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) = notificationService.delete(id)
}
