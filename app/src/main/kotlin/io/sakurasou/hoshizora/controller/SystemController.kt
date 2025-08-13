package io.sakurasou.hoshizora.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.sakurasou.hoshizora.constant.SETTING_READ
import io.sakurasou.hoshizora.controller.vo.CommonResponse
import io.sakurasou.hoshizora.controller.vo.SystemOverviewVO
import io.sakurasou.hoshizora.controller.vo.SystemStatisticsVO
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin

/**
 * @author Shiina Kin
 * 2024/11/18 20:12
 */

fun Route.systemRoute(systemService: io.sakurasou.hoshizora.service.system.SystemService) {
    val controller = SystemController(systemService)
    route("system", {
        tags("system")
        protected = true
    }) {
        install(AuthorizationPlugin) {
            permission = SETTING_READ
        }
        systemStatistics(controller)
        systemOverview(controller)
    }
}

private fun Route.systemStatistics(controller: SystemController) {
    route {
        get("statistics", {
            response {
                HttpStatusCode.OK to {
                    body<CommonResponse<SystemStatisticsVO>> {
                        description = "system statistics"
                    }
                }
            }
        }) {
            val systemStatisticsVO = controller.handleSystemStatistics()
            call.success(systemStatisticsVO)
        }
    }
}

private fun Route.systemOverview(controller: SystemController) {
    route {
        get("overview", {
            response {
                HttpStatusCode.OK to {
                    body<CommonResponse<SystemOverviewVO>> {
                        description = "system overview"
                    }
                }
            }
        }) {
            val systemOverviewVO = controller.handleSystemOverview()
            call.success(systemOverviewVO)
        }
    }
}

class SystemController(
    private val systemService: io.sakurasou.hoshizora.service.system.SystemService,
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
