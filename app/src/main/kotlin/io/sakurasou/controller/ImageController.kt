package io.sakurasou.controller

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


/**
 * @author ShiinaKin
 * 2024/9/5 15:17
 */

fun Route.imageRoute() {
    route("image") {
        get {
            call.respond("hello image")
        }
    }
}

object ImageController {

}