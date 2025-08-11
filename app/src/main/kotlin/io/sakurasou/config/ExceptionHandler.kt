package io.sakurasou.config

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.sakurasou.exception.ServiceThrowable
import io.sakurasou.exception.controller.param.WrongParameterException
import io.sakurasou.extension.failure

/**
 * @author ShiinaKin
 * 2024/10/6 17:26
 */

private val logger = KotlinLogging.logger { }

fun StatusPagesConfig.exceptionHandler() {
    exception<Throwable> { call, cause ->
        if (cause.cause is JsonConvertException && cause.message?.contains("io.sakurasou.controller.request") == true) {
            logger.debug(cause) { "Handle a json convert exception at request" }
            call.failure(WrongParameterException())
            return@exception
        }
        logger.error(cause) { "Handle a unexpected exception" }
        call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
    }
    exception<ServiceThrowable> { call: ApplicationCall, cause ->
        logger.debug(cause) { "Handle a service exception" }
        call.failure(cause)
    }
    exception<RequestValidationException> { call, cause ->
        call.failure(WrongParameterException(cause.message?.substringAfter("Reasons: ")))
    }
}
