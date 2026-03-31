@file:OptIn(ExperimentalKtorApi::class)

package io.sakurasou.hoshizora.controller

import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi
import io.sakurasou.hoshizora.constant.PERMISSION_READ
import io.sakurasou.hoshizora.controller.vo.PermissionVO
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.extension.successResponse
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin
import io.sakurasou.hoshizora.service.permission.PermissionService

/**
 * @author Shiina Kin
 * 2024/12/2 17:54
 */

fun Route.permissionRoutes(permissionService: PermissionService) {
    val controller = PermissionController(permissionService)
    route("permission") {
        fetchAllPermissions(controller)
    }.describe {
        tag("Permission")
    }
}

private fun Route.fetchAllPermissions(controller: PermissionController) {
    install(AuthorizationPlugin) {
        permission = PERMISSION_READ
    }
    get("all") {
        val allPermissions = controller.handleFetchAllPermissions()
        call.success(allPermissions)
    }.describe {
        description = "Fetch all permissions."
        responses {
            successResponse<List<PermissionVO>>()
        }
    }
}

class PermissionController(
    private val permissionService: PermissionService,
) {
    suspend fun handleFetchAllPermissions(): List<PermissionVO> {
        val allPermissions = permissionService.fetchAllPermissions()
        return allPermissions
    }
}
