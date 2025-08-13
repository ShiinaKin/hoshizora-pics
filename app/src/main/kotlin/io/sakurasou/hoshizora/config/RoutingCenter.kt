package io.sakurasou.hoshizora.config

import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.sakurasou.hoshizora.controller.albumRoute
import io.sakurasou.hoshizora.controller.authRoute
import io.sakurasou.hoshizora.controller.commonSiteSettingRoute
import io.sakurasou.hoshizora.controller.groupRoute
import io.sakurasou.hoshizora.controller.imageRoute
import io.sakurasou.hoshizora.controller.permissionRoutes
import io.sakurasou.hoshizora.controller.personalAccessTokenRoute
import io.sakurasou.hoshizora.controller.roleRoute
import io.sakurasou.hoshizora.controller.settingRoute
import io.sakurasou.hoshizora.controller.siteInitRoute
import io.sakurasou.hoshizora.controller.strategyRoute
import io.sakurasou.hoshizora.controller.systemRoute
import io.sakurasou.hoshizora.controller.userRoute
import io.sakurasou.hoshizora.di.InstanceCenter.albumService
import io.sakurasou.hoshizora.di.InstanceCenter.authService
import io.sakurasou.hoshizora.di.InstanceCenter.groupService
import io.sakurasou.hoshizora.di.InstanceCenter.imageService
import io.sakurasou.hoshizora.di.InstanceCenter.permissionService
import io.sakurasou.hoshizora.di.InstanceCenter.personalAccessTokenService
import io.sakurasou.hoshizora.di.InstanceCenter.roleService
import io.sakurasou.hoshizora.di.InstanceCenter.settingService
import io.sakurasou.hoshizora.di.InstanceCenter.strategyService
import io.sakurasou.hoshizora.di.InstanceCenter.systemService
import io.sakurasou.hoshizora.di.InstanceCenter.userService
import io.sakurasou.hoshizora.plugins.SiteInitCheckPlugin

/**
 * @author ShiinaKin
 * 2024/10/6 17:22
 */

fun Route.apiRoute() {
    route("api") {
        route({ tags("common") }) {
            siteInitRoute()
            commonSiteSettingRoute()
        }
        route {
            install(SiteInitCheckPlugin)
            authRoute(authService, userService)
            authenticate("auth-jwt") {
                imageRoute(imageService)
                albumRoute(albumService)
                strategyRoute(strategyService)
                settingRoute(settingService)
                userRoute(userService)
                groupRoute(groupService)
                roleRoute(roleService)
                permissionRoutes(permissionService)
                personalAccessTokenRoute(personalAccessTokenService)
                systemRoute(systemService)
            }
        }
    }
}
