package io.sakurasou.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.sakurasou.config.apiRoute
import io.sakurasou.config.exceptionHandler

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) { exceptionHandler() }
    install(AutoHeadResponse)
    install(DoubleReceive)
    routing {
        apiRoute()
        staticResources("", "static")
        // post("/double-receive") {
        //     val first = call.receiveText()
        //     val theSame = call.receiveText()
        //     call.respondText(first + " " + theSame)
        // }
    }
}