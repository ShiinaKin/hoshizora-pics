package io.sakurasou.config

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.sakurasou.exception.ServiceThrowable
import io.sakurasou.extension.failure

/**
 * @author ShiinaKin
 * 2024/10/6 17:26
 */

private val logger = KotlinLogging.logger {}

fun StatusPagesConfig.exceptionHandler() {
    exception<Throwable> { call, cause ->
        logger.error(cause) { "Handle a unexpected exception" }
        call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
    }
    exception<ServiceThrowable> { call: ApplicationCall, cause ->
        logger.error(cause) { "Handle a service exception" }
        call.failure(cause)
    }
}