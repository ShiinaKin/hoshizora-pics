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
import io.sakurasou.hoshizora.di.inject
import io.sakurasou.hoshizora.plugins.SiteInitCheckPlugin
import io.sakurasou.hoshizora.service.album.AlbumService
import io.sakurasou.hoshizora.service.auth.AuthService
import io.sakurasou.hoshizora.service.common.CommonService
import io.sakurasou.hoshizora.service.group.GroupService
import io.sakurasou.hoshizora.service.image.ImageService
import io.sakurasou.hoshizora.service.permission.PermissionService
import io.sakurasou.hoshizora.service.personalAccessToken.PersonalAccessTokenService
import io.sakurasou.hoshizora.service.role.RoleService
import io.sakurasou.hoshizora.service.setting.SettingService
import io.sakurasou.hoshizora.service.strategy.StrategyService
import io.sakurasou.hoshizora.service.system.SystemService
import io.sakurasou.hoshizora.service.user.UserService
import kotlin.getValue

/**
 * @author ShiinaKin
 * 2024/10/6 17:22
 */

fun Route.apiRoute() {
    val authService: AuthService by inject()
    val userService: UserService by inject()
    val imageService: ImageService by inject()
    val albumService: AlbumService by inject()
    val strategyService: StrategyService by inject()
    val settingService: SettingService by inject()
    val groupService: GroupService by inject()
    val roleService: RoleService by inject()
    val permissionService: PermissionService by inject()
    val personalAccessTokenService: PersonalAccessTokenService by inject()
    val commonService by inject<CommonService>()
    val systemService: SystemService by inject()

    route("api") {
        route({ tags("common") }) {
            siteInitRoute(commonService)
            commonSiteSettingRoute(commonService)
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
