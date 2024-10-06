package io.sakurasou.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import io.sakurasou.exception.ServiceThrowable
import io.sakurasou.extension.failure
import org.jetbrains.exposed.sql.exposedLogger

/**
 * @author ShiinaKin
 * 2024/10/6 17:26
 */
fun StatusPagesConfig.exceptionHandler() {
    exception<Throwable> { call, cause ->
        exposedLogger.error(cause)
        call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
    }
    exception<ServiceThrowable> { call: ApplicationCall, cause ->
        call.failure(cause)
    }
}