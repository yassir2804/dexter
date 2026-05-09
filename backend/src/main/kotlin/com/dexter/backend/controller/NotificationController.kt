package com.dexter.backend.controller

import com.dexter.backend.dto.NotificationDetails
import com.dexter.backend.service.NotificationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("\${url.notifications}")
class NotificationController(@Autowired val notificationService: NotificationService) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findByUser(@RequestParam userId: Long): List<NotificationDetails> =
        notificationService.findByUser(userId)

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun markRead(@PathVariable id: Long) = notificationService.markRead(id)
}
