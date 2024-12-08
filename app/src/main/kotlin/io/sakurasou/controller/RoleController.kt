package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.sakurasou.constant.ROLE_READ_ALL
import io.sakurasou.constant.ROLE_READ_SELF
import io.sakurasou.constant.ROLE_WRITE_ALL
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.request.RoleInsertRequest
import io.sakurasou.controller.request.RolePatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.RolePageVO
import io.sakurasou.controller.vo.RoleVO
import io.sakurasou.exception.controller.param.WrongParameterException
import io.sakurasou.extension.getPrincipal
import io.sakurasou.extension.pageRequest
import io.sakurasou.extension.pageRequestSpec
import io.sakurasou.extension.success
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.role.RoleService

/**
 * @author Shiina Kin
 * 2024/9/9 08:53
 */

fun Route.roleRoute(roleService: RoleService) {
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
                if (insertRequest.name.isBlank()) ValidationResult.Invalid("name is invalid")
                else if (insertRequest.displayName.isBlank()) ValidationResult.Invalid("displayName is invalid")
                else ValidationResult.Valid
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
                if (patchRequest.displayName == null && patchRequest.description == null)
                    ValidationResult.Invalid("at least one field is required")
                else if (patchRequest.displayName != null) ValidationResult.Invalid("displayName is invalid")
                else ValidationResult.Valid
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
    private val roleService: RoleService
) {
    suspend fun handleInsertRole(insertRequest: RoleInsertRequest) {
        roleService.saveRole(insertRequest)
    }

    suspend fun handlePatchRole(name: String, patchRequest: RolePatchRequest) {
        roleService.patchRole(name, patchRequest)
    }

    suspend fun handleDeleteRole(name: String) {
        roleService.deleteRole(name)
    }

    suspend fun handleFetchRole(name: String): RoleVO {
        return roleService.fetchRole(name)
    }

    suspend fun handleListAllRolesWithPermissionsOfUser(groupId: Long): List<RoleVO> {
        return roleService.listRolesWithPermissionsOfUser(groupId)
    }

    suspend fun handlePageRoles(pageRequest: PageRequest): PageResult<RolePageVO> {
        return roleService.pageRoles(pageRequest)
    }
}