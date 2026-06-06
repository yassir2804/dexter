package com.dexter.backend.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    private fun buildResponseEntity(apiError: ApiError): ResponseEntity<Any> =
        ResponseEntity(apiError, apiError.status)

    @ExceptionHandler(NoSuchElementException::class)
    fun elementNotFound(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        val apiError = ApiError(
            message = "error occurred",
            debugMessage = ex.message,
            status = HttpStatus.NOT_FOUND,
        )
        apiError.addSubError(ApiSubError("ELEMENT_NOT_FOUND", "Element not found"))
        logger.debug("BackEnd - Dexter {}", ex)
        return buildResponseEntity(apiError)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        val apiError = ApiError(
            message = "bad request",
            debugMessage = ex.message,
            status = HttpStatus.BAD_REQUEST,
        )
        apiError.addSubError(ApiSubError("BAD_REQUEST", ex.message ?: "Invalid input"))
        return buildResponseEntity(apiError)
    }

    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        val apiError = ApiError(
            message = "error occurred",
            debugMessage = ex.message,
            status = HttpStatus.INTERNAL_SERVER_ERROR,
        )
        apiError.addSubError(ApiSubError("INTERNAL_ERROR", "There is a serious error in the system"))
        logger.error("BackEnd - Dexter unhandled exception", ex)
        return buildResponseEntity(apiError)
    }
}
