package io.sakurasou.plugins

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.route
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
import io.sakurasou.config.InstanceCenter.authService
import io.sakurasou.config.InstanceCenter.commonService
import io.sakurasou.config.InstanceCenter.userService
import io.sakurasou.controller.*
import io.sakurasou.exception.ServiceThrowable
import io.sakurasou.exception.SiteNotInitializationException
import io.sakurasou.extension.failure
import io.sakurasou.extension.getPrincipal
import io.sakurasou.extension.isSiteNotInitialized

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
        exception<ServiceThrowable> { call: ApplicationCall, cause ->
            call.failure(cause)
        }
        // TODO handle all custom exceptions
    }
    install(AutoHeadResponse)
    install(DoubleReceive)
    routing {
        route("api") {
            siteInitRoute()

            route {
                intercept(ApplicationCallPipeline.Setup) {
                    if (isSiteNotInitialized()) throw SiteNotInitializationException()
                }
                authRoute(authService, userService)
                commonRoute(commonService)
                authenticate("auth-jwt") {
                    intercept(ApplicationCallPipeline.Call) {
                        val principal = call.principal<JWTPrincipal>()!!
                        val id = principal.payload.getClaim("id").asLong()
                        val groupId = principal.payload.getClaim("groupId").asLong()
                        val username = principal.payload.getClaim("username").asString()
                        val roles: List<String> = principal.payload.getClaim("roles").asList(String::class.java)
                        call.attributes.put(AttributeKey("id"), id)
                        call.attributes.put(AttributeKey("groupId"), groupId)
                        call.attributes.put(AttributeKey("username"), username)
                        call.attributes.put(AttributeKey("roles"), roles)
                    }
                    imageRoute()
                    albumRoute()
                    strategyRoute()
                    settingRoute()
                    userRoute(userService)
                    groupRoute()
                    roleRoute()
                }
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