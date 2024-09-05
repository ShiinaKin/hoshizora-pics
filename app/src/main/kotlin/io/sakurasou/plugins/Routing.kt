package io.sakurasou.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.webjars.*
import io.sakurasou.controller.imageRoute
import io.sakurasou.controller.userRoute
import kotlinx.serialization.Serializable
import java.io.File

fun Application.configureRouting() {
    install(Webjars) {
        path = "/webjars" // defaults to /webjars
    }
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    install(AutoHeadResponse)
    install(DoubleReceive)
    install(SwaggerUI) {
        info {
            title = "Example API"
            version = "latest"
            description = "Example API for testing and demonstration purposes."
        }
        server {
            url = "http://localhost:8080"
            description = "Development Server"
        }
    }
    routing {
        route("api") {
            get {
                call.respondText("Hello World!")
            }
            imageRoute()
            userRoute()
        }
        route("openapi.json") {
            openApiSpec()
        }
        route("swagger") {
            swaggerUI("/openapi.json")
        }
        get("webjars") {
            call.respondText("<script src='/webjars/jquery/jquery.js'></script>", ContentType.Text.Html)
        }
        staticResources("", "static")
        post("/double-receive") {
            val first = call.receiveText()
            val theSame = call.receiveText()
            call.respondText(first + " " + theSame)
        }
    }
}