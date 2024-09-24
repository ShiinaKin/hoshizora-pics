package io.sakurasou.plugins

import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import io.sakurasou.config.InstanceCenter.authService
import io.sakurasou.config.InstanceCenter.commonService
import io.sakurasou.config.InstanceCenter.groupService
import io.sakurasou.config.InstanceCenter.roleService
import io.sakurasou.config.InstanceCenter.settingService
import io.sakurasou.config.InstanceCenter.strategyService
import io.sakurasou.config.InstanceCenter.userService
import io.sakurasou.controller.*
import io.sakurasou.exception.ServiceThrowable
import io.sakurasou.exception.SiteNotInitializationException
import io.sakurasou.extension.failure
import io.sakurasou.extension.isSiteNotInitialized
import org.jetbrains.exposed.sql.exposedLogger

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            exposedLogger.error(cause)
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
        exception<ServiceThrowable> { call: ApplicationCall, cause ->
            call.failure(cause)
        }
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
                    imageRoute()
                    albumRoute()
                    strategyRoute(strategyService)
                    settingRoute(settingService)
                    userRoute(userService)
                    groupRoute(groupService)
                    roleRoute(roleService)
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