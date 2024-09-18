package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.sakurasou.constant.ROLE_READ_ALL
import io.sakurasou.constant.ROLE_READ_SELF
import io.sakurasou.controller.vo.RoleVO
import io.sakurasou.extension.getPrincipal
import io.sakurasou.extension.success
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.role.RoleService

/**
 * @author Shiina Kin
 * 2024/9/9 08:53
 */

fun Route.roleRoute(roleService: RoleService) {
    val controller = RoleController(roleService)
    route("role") {
        fetchAllRolesAndPermissions(controller)
        fetchAllRolesAndPermissionsOfUser(controller)
    }
}

private fun Route.fetchAllRolesAndPermissions(controller: RoleController) {
    route {
        install(AuthorizationPlugin) {
            permission = ROLE_READ_ALL
        }
        get("all", {
            protected = true
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<List<RoleVO>> {
                        description = "all roles with permissions"
                    }
                }
            }
        }) {
            val rolesWithPermissions = controller.handleListAllRolesWithPermissions()
            call.success(rolesWithPermissions)
        }
    }
}

private fun Route.fetchAllRolesAndPermissionsOfUser(controller: RoleController) {
    route {
        install(AuthorizationPlugin) {
            permission = ROLE_READ_SELF
        }
        get("self", {
            protected = true
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<List<RoleVO>> {
                        description = "all roles with permissions of user"
                    }
                }
            }
        }) {
            val principal = call.getPrincipal()
            val rolesWithPermissions = controller.handleListAllRolesWithPermissionsOfUser(principal.roles)
            call.success(rolesWithPermissions)
        }
    }
}

class RoleController(
    private val roleService: RoleService
) {
    suspend fun handleListAllRolesWithPermissions(): Map<String, RoleVO> {
        return roleService.listRolesWithPermissions()
    }

    suspend fun handleListAllRolesWithPermissionsOfUser(roles: List<String>): Map<String, RoleVO> {
        return roleService.listRolesWithPermissionsOfUser(roles)
    }
}