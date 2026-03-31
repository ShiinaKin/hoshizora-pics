@file:OptIn(ExperimentalKtorApi::class)

package io.sakurasou.hoshizora.controller

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi
import io.sakurasou.hoshizora.constant.SETTING_READ
import io.sakurasou.hoshizora.controller.vo.SystemOverviewVO
import io.sakurasou.hoshizora.controller.vo.SystemStatisticsVO
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.extension.successResponse
import io.sakurasou.hoshizora.extension.transparentRoute
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin
import io.sakurasou.hoshizora.service.system.SystemService

/**
 * @author Shiina Kin
 * 2024/11/18 20:12
 */

fun Route.systemRoute(systemService: SystemService) {
    val controller = SystemController(systemService)
    route("system") {
        install(AuthorizationPlugin) {
            permission = SETTING_READ
        }
        systemStatistics(controller)
        systemOverview(controller)
    }.describe {
        tag("system")
    }
}

private fun Route.systemStatistics(controller: SystemController) {
    transparentRoute {
        get("statistics") {
            val systemStatisticsVO = controller.handleSystemStatistics()
            call.success(systemStatisticsVO)
        }.describe {
            responses {
                successResponse<SystemStatisticsVO>("system statistics")
            }
        }
    }
}

private fun Route.systemOverview(controller: SystemController) {
    transparentRoute {
        get("overview") {
            val systemOverviewVO = controller.handleSystemOverview()
            call.success(systemOverviewVO)
        }.describe {
            responses {
                successResponse<SystemOverviewVO>("system overview")
            }
        }
    }
}

class SystemController(
    private val systemService: SystemService,
) {
    suspend fun handleSystemStatistics(): SystemStatisticsVO {
        val systemStatisticsVO = systemService.fetchSystemStatistics()
        return systemStatisticsVO
    }

    suspend fun handleSystemOverview(): SystemOverviewVO {
        val systemOverviewVO = systemService.fetchSystemOverview()
        return systemOverviewVO
    }
}
