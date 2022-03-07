package ru.bastrich.controllers

import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.reflect.UndeclaredThrowableException


@ControllerAdvice
class PromoplanExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [Exception::class])
    protected fun handleError(e: Exception, request: WebRequest): ResponseEntity<Any> {
        val realException = if (e is UndeclaredThrowableException) e.undeclaredThrowable else e

        handlerLogger.error(realException) { "Game error" }

        return handleExceptionInternal(
            e,
            ErrorResponse(realException.message),
            HttpHeaders(),
            HttpStatus.INTERNAL_SERVER_ERROR,
            request
        )
    }
}

data class ErrorResponse(
    val message: String?
)

private val handlerLogger = KotlinLogging.logger {}

