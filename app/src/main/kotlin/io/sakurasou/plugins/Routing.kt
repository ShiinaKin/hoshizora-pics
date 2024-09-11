package io.sakurasou.plugins

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
    routing {
        route("api") {
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
            // cacheOutput(2.seconds) {
            //     get("/short") {
            //         call.respond(Random.nextInt().toString())
            //     }
            // }
            // cacheOutput {
            //     get("/default") {
            //         call.respond(Random.nextInt().toString())
            //     }
            // }
        }
        staticResources("", "static")
        // post("/double-receive") {
        //     val first = call.receiveText()
        //     val theSame = call.receiveText()
        //     call.respondText(first + " " + theSame)
        // }
    }
}