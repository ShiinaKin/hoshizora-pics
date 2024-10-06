package io.sakurasou.config

import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.sakurasou.config.InstanceCenter.albumService
import io.sakurasou.config.InstanceCenter.authService
import io.sakurasou.config.InstanceCenter.commonService
import io.sakurasou.config.InstanceCenter.groupService
import io.sakurasou.config.InstanceCenter.imageService
import io.sakurasou.config.InstanceCenter.roleService
import io.sakurasou.config.InstanceCenter.settingService
import io.sakurasou.config.InstanceCenter.strategyService
import io.sakurasou.config.InstanceCenter.userService
import io.sakurasou.controller.*
import io.sakurasou.exception.controller.status.SiteNotInitializationException
import io.sakurasou.extension.isSiteNotInitialized

/**
 * @author ShiinaKin
 * 2024/10/6 17:22
 */

fun Route.apiRoute() {
    route("api") {
        siteInitRoute()
        route {
            intercept(ApplicationCallPipeline.Setup) {
                if (isSiteNotInitialized()) throw SiteNotInitializationException()
            }
            authRoute(authService, userService)
            commonRoute(commonService)
            authenticate("auth-jwt") {
                imageRoute(imageService)
                albumRoute(albumService)
                strategyRoute(strategyService)
                settingRoute(settingService)
                userRoute(userService)
                groupRoute(groupService)
                roleRoute(roleService)
            }
        }
    }
}