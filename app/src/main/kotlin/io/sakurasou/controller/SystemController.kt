package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.routing.*
import io.sakurasou.constant.SETTING_READ
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.SystemOverviewVO
import io.sakurasou.extension.success
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.system.SystemService

/**
 * @author Shiina Kin
 * 2024/11/18 20:12
 */

fun Route.systemRoute(systemService: SystemService) {
    val controller = SystemController(systemService)
    route("system", {
        tags("system")
        protected = true
    }) {
        install(AuthorizationPlugin) {
            permission = SETTING_READ
        }
        systemOverview(controller)
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
    private val systemService: SystemService
) {
    suspend fun handleSystemOverview(): SystemOverviewVO {
        val systemOverviewVO = systemService.fetchSystemOverview()
        return systemOverviewVO
    }
}