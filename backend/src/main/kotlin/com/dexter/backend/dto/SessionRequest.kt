package com.dexter.backend.dto

data class SessionRequest(
    val title: String,
    val description: String?,
    val scheduledAt: Long?,
)
