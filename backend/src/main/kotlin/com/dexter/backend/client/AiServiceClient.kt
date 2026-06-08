package com.dexter.backend.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

data class AiPlanRequest(
    val userAName: String,
    val userAOffers: List<String>,
    val userBName: String,
    val userBOffers: List<String>,
    val availableDays: List<String>,
    val overlapDayNumbers: List<Int> = emptyList(),
    val userADayNumbers: List<Int> = emptyList(),
    val userBDayNumbers: List<Int> = emptyList(),
    val timezone: String? = null
)

data class AiSessionPlan(
    val sessionNumber: Int,
    val title: String,
    val description: String,
    val skill: String,
    val scheduledDate: String? = null
)

data class AiScheduleRequest(
    val userAName: String,
    val userBName: String,
    val userAAvailability: List<Map<String, Any>>,
    val userBAvailability: List<Map<String, Any>>,
    val sessionTitle: String
)

data class AiScheduleSlot(
    val day: String,
    val time: String
)

@Component
class AiServiceClient(
    @Value("\${ai.service.url:http://localhost:8001}") private val baseUrl: String
) {
    private val log = LoggerFactory.getLogger(AiServiceClient::class.java)
    private val restTemplate = RestTemplate()

    fun generatePlan(request: AiPlanRequest): List<AiSessionPlan>? = try {
        val result = restTemplate.postForObject(
            "$baseUrl/generate-plan",
            request,
            Array<AiSessionPlan>::class.java
        )
        result?.toList().also {
            log.info("AI generó ${it?.size} sesiones para ${request.userAName} ↔ ${request.userBName}")
        }
    } catch (e: Exception) {
        log.warn("AI service no disponible, usando títulos genéricos: ${e.message}")
        null
    }

    fun suggestSchedule(request: AiScheduleRequest): List<AiScheduleSlot>? = try {
        val result = restTemplate.postForObject(
            "$baseUrl/suggest-schedule",
            request,
            Array<AiScheduleSlot>::class.java
        )
        result?.toList().also {
            log.info("AI sugirió ${it?.size} horarios para '${request.sessionTitle}'")
        }
    } catch (e: Exception) {
        log.warn("AI service no disponible para sugerencia de horario: ${e.message}")
        null
    }
}
