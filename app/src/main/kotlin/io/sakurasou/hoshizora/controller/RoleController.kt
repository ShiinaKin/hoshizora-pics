package io.sakurasou.hoshizora.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.sakurasou.hoshizora.constant.ROLE_READ_ALL
import io.sakurasou.hoshizora.constant.ROLE_READ_SELF
import io.sakurasou.hoshizora.constant.ROLE_WRITE_ALL
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.request.RoleInsertRequest
import io.sakurasou.hoshizora.controller.request.RolePatchRequest
import io.sakurasou.hoshizora.controller.vo.CommonResponse
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.RolePageVO
import io.sakurasou.hoshizora.controller.vo.RoleVO
import io.sakurasou.hoshizora.exception.controller.param.WrongParameterException
import io.sakurasou.hoshizora.extension.getPrincipal
import io.sakurasou.hoshizora.extension.pageRequest
import io.sakurasou.hoshizora.extension.pageRequestSpec
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin

/**
 * @author Shiina Kin
 * 2024/9/9 08:53
 */

fun Route.roleRoute(roleService: io.sakurasou.hoshizora.service.role.RoleService) {
    val controller = RoleController(roleService)
    route("role", {
        protected = true
        tags("Role")
    }) {
        route("{role_name}", {
            request {
                pathParameter<String>("role_name") {
                    required = true
                    description = "role name"
                }
            }
        }) {
            patchRole(controller)
            deleteRole(controller)
            fetchRole(controller)
        }
        insertRole(controller)
        fetchAllRolesAndPermissionsOfUser(controller)
        pageRoles(controller)
    }
}

private fun Route.insertRole(controller: RoleController) {
    route {
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
        post({
            request {
                body<RoleInsertRequest> {
                    required = true
                    description = "role insert request"
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> {
                        description = "insert role"
                    }
                }
            }
        }) {
            val insertRequest = call.receive<RoleInsertRequest>()
            controller.handleInsertRole(insertRequest)
            call.success()
        }
    }
}

private fun Route.patchRole(controller: RoleController) {
    route {
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
        patch({
            request {
                body<RolePatchRequest> {
                    required = true
                    description = "role patch request"
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> {
                        description = "patch role"
                    }
                }
            }
        }) {
            val name = call.parameters["role_name"] ?: throw WrongParameterException("role name")
            val patchRequest = call.receive<RolePatchRequest>()
            controller.handlePatchRole(name, patchRequest)
            call.success()
        }
    }
}

private fun Route.deleteRole(controller: RoleController) {
    route {
        install(AuthorizationPlugin) {
            permission = ROLE_WRITE_ALL
        }
        delete({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> {
                        description = "delete role"
                    }
                }
            }
        }) {
            val name = call.parameters["role_name"] ?: throw WrongParameterException("role name")
            controller.handleDeleteRole(name)
            call.success()
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
                    body<CommonResponse<List<RoleVO>>> {
                        description = "all roles with permissions of user"
                    }
                }
            }
        }) {
            val principal = call.getPrincipal()
            val rolesWithPermissions = controller.handleListAllRolesWithPermissionsOfUser(principal.groupId)
            call.success(rolesWithPermissions)
        }
    }
}

private fun Route.fetchRole(controller: RoleController) {
    route {
        install(AuthorizationPlugin) {
            permission = ROLE_READ_ALL
        }
        get({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<RoleVO>> {
                        description = "role with permissions"
                    }
                }
            }
        }) {
            val name = call.parameters["role_name"] ?: throw WrongParameterException("role name")
            val role = controller.handleFetchRole(name)
            call.success(role)
        }
    }
}

private fun Route.pageRoles(controller: RoleController) {
    route {
        install(AuthorizationPlugin) {
            permission = ROLE_READ_ALL
        }
        get("page", {
            pageRequestSpec()
            response {
                HttpStatusCode.OK to {
                    body<CommonResponse<PageResult<RolePageVO>>> {
                        description = "Success"
                    }
                }
            }
        }) {
            val pageRequest = call.pageRequest()
            val pageResult = controller.handlePageRoles(pageRequest)
            call.success(pageResult)
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
