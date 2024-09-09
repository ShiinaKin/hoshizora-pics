package io.sakurasou.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.sakurasou.controller.*
import io.sakurasou.exception.FileSizeException
import io.sakurasou.exception.WrongParameterException

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
        exception<WrongParameterException> { call: ApplicationCall, cause ->
            call.respondText(text = "400: $cause", status = HttpStatusCode.BadRequest)
        }
        exception<FileSizeException> { call: ApplicationCall, cause ->
            call.respondText(text = "400: $cause", status = HttpStatusCode.BadRequest)
        }
    }
    install(AutoHeadResponse)
    install(DoubleReceive)
    install(SwaggerUI) {
        info {
            title = "HoshizoraPics API"
            version = "latest"
            description = "API for testing and demonstration purposes."
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
            // authenticate {
            //
            // }
            commonRoute()
            imageRoute()
            albumRoute()
            strategyRoute()
            settingRoute()
            userRoute()
            groupRoute()
            roleRoute()
            route("openapi.json") {
                openApiSpec()
            }
            route("swagger") {
                swaggerUI("/api/openapi.json")
            }
        }
        staticResources("", "static")
        // post("/double-receive") {
        //     val first = call.receiveText()
        //     val theSame = call.receiveText()
        //     call.respondText(first + " " + theSame)
        // }
    }
}