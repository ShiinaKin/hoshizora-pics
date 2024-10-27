package io.sakurasou.config

import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.sakurasou.di.InstanceCenter.albumService
import io.sakurasou.di.InstanceCenter.authService
import io.sakurasou.di.InstanceCenter.commonService
import io.sakurasou.di.InstanceCenter.groupService
import io.sakurasou.di.InstanceCenter.imageService
import io.sakurasou.di.InstanceCenter.roleService
import io.sakurasou.di.InstanceCenter.settingService
import io.sakurasou.di.InstanceCenter.strategyService
import io.sakurasou.di.InstanceCenter.userService
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