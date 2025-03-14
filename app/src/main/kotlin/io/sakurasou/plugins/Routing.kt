package io.sakurasou.plugins

import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import io.sakurasou.config.apiRoute
import io.sakurasou.config.exceptionHandler
import io.sakurasou.controller.commonRoute
import io.sakurasou.di.InstanceCenter.commonService
import kotlin.time.Duration.Companion.seconds

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, _ ->
            if (call.request.uri.startsWith("/api")
                || call.request.uri.startsWith("/s")
                || call.request.uri.startsWith("/random")
            ) call.respond(HttpStatusCode.NotFound)
            else call.respondSource(call.resolveResource("index.html", "static")!!.readFrom().readRemaining())
        }
        exceptionHandler()
    }
    install(AutoHeadResponse)
    install(RateLimit) {
        register(RateLimitName("randomFetchLimit")) { rateLimiter(limit = 20, refillPeriod = 2.seconds) }
        register(RateLimitName("anonymousGetLimit")) { rateLimiter(limit = 20, refillPeriod = 1.seconds) }
    }
    routing {
        apiRoute()
        route {
            install(SiteInitCheckPlugin)
            commonRoute(commonService)
        }
        route({ hidden = true }) { staticResources("", "static") }
    }
}