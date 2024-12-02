package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.sakurasou.constant.PERMISSION_READ
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PermissionVO
import io.sakurasou.extension.success
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.permission.PermissionService

/**
 * @author Shiina Kin
 * 2024/12/2 17:54
 */

fun Route.permissionRoutes(permissionService: PermissionService) {
    val controller = PermissionController(permissionService)
    route("permission", {
        protected = true
        tags("Permission")
    }) {
        fetchAllPermissions(controller)
    }
}

private fun Route.fetchAllPermissions(controller: PermissionController) {
    route {
        install(AuthorizationPlugin) {
            permission = PERMISSION_READ
        }
        get("all", {
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<List<PermissionVO>>>()
                }
            }
        }) {
            val allPermissions = controller.handleFetchAllPermissions()
            call.success(allPermissions)
        }
    }
}

class PermissionController(
    private val permissionService: PermissionService
) {
    suspend fun handleFetchAllPermissions(): List<PermissionVO> {
        val allPermissions = permissionService.fetchAllPermissions()
        return allPermissions
    }
}
