@file:OptIn(ExperimentalKtorApi::class)

package io.sakurasou.hoshizora.controller

import io.ktor.openapi.jsonSchema
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi
import io.sakurasou.hoshizora.constant.ROLE_READ_ALL
import io.sakurasou.hoshizora.constant.ROLE_READ_SELF
import io.sakurasou.hoshizora.constant.ROLE_WRITE_ALL
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.request.RoleInsertRequest
import io.sakurasou.hoshizora.controller.request.RolePatchRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.RolePageVO
import io.sakurasou.hoshizora.controller.vo.RoleVO
import io.sakurasou.hoshizora.exception.controller.param.WrongParameterException
import io.sakurasou.hoshizora.extension.getPrincipal
import io.sakurasou.hoshizora.extension.pageRequest
import io.sakurasou.hoshizora.extension.pageRequestSpec
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.extension.successResponse
import io.sakurasou.hoshizora.extension.transparentRoute
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin

/**
 * @author Shiina Kin
 * 2024/9/9 08:53
 */

fun Route.roleRoute(roleService: io.sakurasou.hoshizora.service.role.RoleService) {
    val controller = RoleController(roleService)
    route("role") {
        route("{role_name}") {
            patchRole(controller)
            deleteRole(controller)
            fetchRole(controller)
        }.describe {
            parameters {
                path("role_name") {
                    required = true
                    description = "role name"
                    schema = jsonSchema<String>()
                }
            }
        }
        insertRole(controller)
        fetchAllRolesAndPermissionsOfUser(controller)
        pageRoles(controller)
    }.describe {
        tag("Role")
    }
}

private fun Route.insertRole(controller: RoleController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ROLE_WRITE_ALL
        }
        install(RequestValidation) {
            validate<RoleInsertRequest> { insertRequest ->
                if (insertRequest.name.isBlank()) {
                    ValidationResult.Invalid("name is invalid")
                } else if (insertRequest.displayName.isBlank()) {
                    ValidationResult.Invalid("displayName is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        post {
            val insertRequest = call.receive<RoleInsertRequest>()
            controller.handleInsertRole(insertRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                description = "role insert request"
                schema = jsonSchema<RoleInsertRequest>()
            }
            responses {
                successResponse<Unit>("insert role")
            }
        }
    }
}

private fun Route.patchRole(controller: RoleController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ROLE_WRITE_ALL
        }
        install(RequestValidation) {
            validate<RolePatchRequest> { patchRequest ->
                if (patchRequest.displayName == null && patchRequest.description == null) {
                    ValidationResult.Invalid("at least one field is required")
                } else if (patchRequest.displayName != null) {
                    ValidationResult.Invalid("displayName is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch {
            val name = call.parameters["role_name"] ?: throw WrongParameterException("role name")
            val patchRequest = call.receive<RolePatchRequest>()
            controller.handlePatchRole(name, patchRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                description = "role patch request"
                schema = jsonSchema<RolePatchRequest>()
            }
            responses {
                successResponse<Unit>("patch role")
            }
        }
    }
}

private fun Route.deleteRole(controller: RoleController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ROLE_WRITE_ALL
        }
        delete {
            val name = call.parameters["role_name"] ?: throw WrongParameterException("role name")
            controller.handleDeleteRole(name)
            call.success()
        }.describe {
            responses {
                successResponse<Unit>("delete role")
            }
        }
    }
}

private fun Route.fetchAllRolesAndPermissionsOfUser(controller: RoleController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ROLE_READ_SELF
        }
        get("self") {
            val principal = call.getPrincipal()
            val rolesWithPermissions = controller.handleListAllRolesWithPermissionsOfUser(principal.groupId)
            call.success(rolesWithPermissions)
        }.describe {
            responses {
                successResponse<List<RoleVO>>("all roles with permissions of user")
            }
        }
    }
}

private fun Route.fetchRole(controller: RoleController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ROLE_READ_ALL
        }
        get {
            val name = call.parameters["role_name"] ?: throw WrongParameterException("role name")
            val role = controller.handleFetchRole(name)
            call.success(role)
        }.describe {
            responses {
                successResponse<RoleVO>("role with permissions")
            }
        }
    }
}

private fun Route.pageRoles(controller: RoleController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ROLE_READ_ALL
        }
        get("page") {
            val pageRequest = call.pageRequest()
            val pageResult = controller.handlePageRoles(pageRequest)
            call.success(pageResult)
        }.describe {
            pageRequestSpec()
            responses {
                successResponse<PageResult<RolePageVO>>("Success")
            }
        }
    }
}

class RoleController(
    private val roleService: io.sakurasou.hoshizora.service.role.RoleService,
) {
    suspend fun handleInsertRole(insertRequest: RoleInsertRequest) {
        roleService.saveRole(insertRequest)
    }

    suspend fun handlePatchRole(
        name: String,
        patchRequest: RolePatchRequest,
    ) {
        roleService.patchRole(name, patchRequest)
    }

    suspend fun handleDeleteRole(name: String) {
        roleService.deleteRole(name)
    }

    suspend fun handleFetchRole(name: String): RoleVO = roleService.fetchRole(name)

    suspend fun handleListAllRolesWithPermissionsOfUser(groupId: Long): List<RoleVO> = roleService.listRolesWithPermissionsOfUser(groupId)

    suspend fun handlePageRoles(pageRequest: PageRequest): PageResult<RolePageVO> = roleService.pageRoles(pageRequest)
}
