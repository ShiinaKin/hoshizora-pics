package io.sakurasou.plugins

import com.ucasoft.ktor.simpleCache.cacheOutput
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.sakurasou.config.InstanceCenter.relationDao
import io.sakurasou.config.InstanceCenter.userDao
import io.sakurasou.controller.*
import io.sakurasou.exception.FileSizeException
import io.sakurasou.exception.UnauthorizedAccessException
import io.sakurasou.exception.UserNotFoundException
import io.sakurasou.exception.WrongParameterException
import io.sakurasou.extension.getPrincipal

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
        exception<UnauthorizedAccessException> { call: ApplicationCall, cause ->
            call.respondText(text = "401: $cause", status = HttpStatusCode.Unauthorized)
        }
        exception<UserNotFoundException> { call: ApplicationCall, cause ->
            call.respondText(text = "400: $cause", status = HttpStatusCode.BadRequest)
        }
    }
    install(AutoHeadResponse)
    install(DoubleReceive)
    routing {
        route("api") {
            authRoute(userDao, relationDao)
            cacheOutput { commonRoute() }
            authenticate("auth-jwt") {
                intercept(ApplicationCallPipeline.Call) {
                    val principal = call.principal<JWTPrincipal>()
                    val username = principal!!.payload.getClaim("username").asString()
                    val role: List<String> = principal.payload.getClaim("role").asList(String::class.java)
                    call.attributes.put(AttributeKey("username"), username)
                    call.attributes.put(AttributeKey("role"), role)
                }
                get("helloworld") {
                    call.respond(call.attributes.getPrincipal())
                }
                imageRoute()
                albumRoute()
                strategyRoute()
                settingRoute()
                userRoute(userDao)
                groupRoute()
                roleRoute()
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