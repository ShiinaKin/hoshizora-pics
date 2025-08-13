package io.sakurasou.hoshizora.plugins

import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.resolveResource
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.response.respondSource
import io.ktor.server.routing.routing
import io.ktor.utils.io.readRemaining
import io.sakurasou.hoshizora.config.apiRoute
import io.sakurasou.hoshizora.config.exceptionHandler
import io.sakurasou.hoshizora.controller.commonRoute
import io.sakurasou.hoshizora.di.InstanceCenter.commonService
import kotlin.time.Duration.Companion.seconds

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, _ ->
            if (call.request.uri.startsWith("/api") ||
                call.request.uri.startsWith("/s") ||
                call.request.uri.startsWith("/random")
            ) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respondSource(call.resolveResource("index.html", "static")!!.readFrom().readRemaining())
            }
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
