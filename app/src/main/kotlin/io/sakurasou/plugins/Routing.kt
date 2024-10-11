package io.sakurasou.plugins

import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.sakurasou.config.apiRoute
import io.sakurasou.config.exceptionHandler

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) { exceptionHandler() }
    install(AutoHeadResponse)
    routing {
        apiRoute()
        route({ hidden = true }) {
            staticResources("", "static")
        }
    }
}