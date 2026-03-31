@file:OptIn(ExperimentalKtorApi::class)

package io.sakurasou.hoshizora.controller

import io.ktor.http.HttpStatusCode
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
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi
import io.sakurasou.hoshizora.constant.GROUP_DELETE
import io.sakurasou.hoshizora.constant.GROUP_READ_ALL
import io.sakurasou.hoshizora.constant.GROUP_READ_SINGLE
import io.sakurasou.hoshizora.constant.GROUP_WRITE
import io.sakurasou.hoshizora.controller.request.GroupInsertRequest
import io.sakurasou.hoshizora.controller.request.GroupPatchRequest
import io.sakurasou.hoshizora.controller.request.GroupPutRequest
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.GroupAllowedImageType
import io.sakurasou.hoshizora.controller.vo.GroupPageVO
import io.sakurasou.hoshizora.controller.vo.GroupVO
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.extension.commonResponse
import io.sakurasou.hoshizora.extension.getPrincipal
import io.sakurasou.hoshizora.extension.id
import io.sakurasou.hoshizora.extension.pageRequest
import io.sakurasou.hoshizora.extension.pageRequestSpec
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.extension.successResponse
import io.sakurasou.hoshizora.extension.transparentRoute
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin
import io.sakurasou.hoshizora.service.group.GroupService

/**
 * @author Shiina Kin
 * 2024/9/9 08:58
 */
fun Route.groupRoute(groupService: GroupService) {
    val controller = GroupController(groupService)
    route("group") {
        groupInsert(controller)
        route("{id}") {
            groupDelete(controller)
            groupUpdate(controller)
            groupPatch(controller)
            groupFetch(controller)
        }.describe {
            parameters {
                path("id") {
                    description = "group id"
                    required = true
                    schema = jsonSchema<Long>()
                }
            }
            responses {
                commonResponse(HttpStatusCode.NotFound, "group not found")
            }
        }
        groupPage(controller)
        groupFetchGroupAllowedImageType(controller)
    }.describe {
        tag("Group")
    }
}

private fun Route.groupInsert(controller: GroupController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = GROUP_WRITE
        }
        install(RequestValidation) {
            validate<GroupInsertRequest> { insertRequest ->
                if (insertRequest.name.isBlank()) {
                    ValidationResult.Invalid("name is invalid")
                } else if (insertRequest.roles.isEmpty()) {
                    ValidationResult.Invalid("roles is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        post {
            val insertRequest = call.receive<GroupInsertRequest>()
            controller.handleInsertGroup(insertRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                schema = jsonSchema<GroupInsertRequest>()
            }
            responses {
                successResponse<Unit>()
                commonResponse(HttpStatusCode.Conflict, "group name conflict")
            }
        }
    }
}

private fun Route.groupDelete(controller: GroupController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = GROUP_DELETE
        }
        delete {
            val id = call.id()
            controller.handleDeleteGroup(id)
            call.success()
        }.describe {
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.groupPatch(controller: GroupController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = GROUP_WRITE
        }
        install(RequestValidation) {
            validate<GroupPatchRequest> { patchRequest ->
                if (patchRequest.name == null &&
                    patchRequest.description == null &&
                    patchRequest.strategyId == null &&
                    patchRequest.config == null &&
                    patchRequest.roles == null
                ) {
                    ValidationResult.Invalid("at least one field is required")
                } else if (patchRequest.name != null && patchRequest.name.isBlank()) {
                    ValidationResult.Invalid("name is invalid")
                } else if (patchRequest.roles != null && patchRequest.roles.isEmpty()) {
                    ValidationResult.Invalid("roles is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch {
            val id = call.id()
            val patchRequest = call.receive<GroupPatchRequest>()
            controller.handlePatchGroup(id, patchRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                schema = jsonSchema<GroupPatchRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.groupUpdate(controller: GroupController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = GROUP_WRITE
        }
        install(RequestValidation) {
            validate<GroupPutRequest> { putRequest ->
                if (putRequest.name.isBlank()) {
                    ValidationResult.Invalid("name is invalid")
                } else if (putRequest.roles.isEmpty()) {
                    ValidationResult.Invalid("roles is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        put {
            val id = call.id()
            val patchRequest = call.receive<GroupPutRequest>()
            controller.handleUpdateGroup(id, patchRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                schema = jsonSchema<GroupPutRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.groupFetch(controller: GroupController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = GROUP_READ_SINGLE
        }
        get {
            val id = call.id()
            val groupVO = controller.handleFetchGroup(id)
            call.success(groupVO)
        }.describe {
            responses {
                successResponse<GroupVO>()
            }
        }
    }
}

private fun Route.groupPage(controller: GroupController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = GROUP_READ_ALL
        }
        get("page") {
            val pageRequest = call.pageRequest()
            val pageResult = controller.handlePageGroups(pageRequest)
            call.success(pageResult)
        }.describe {
            pageRequestSpec()
            responses {
                successResponse<PageResult<GroupPageVO>>("page result")
                HttpStatusCode.BadRequest {
                    description = "page or pageSize wrong"
                }
            }
        }
    }
}

private fun Route.groupFetchGroupAllowedImageType(controller: GroupController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = GROUP_READ_SINGLE
        }
        get("type") {
            val groupId = call.getPrincipal().groupId
            val groupVO = controller.handleFetchGroupAllowedImageType(groupId)
            call.success(groupVO)
        }.describe {
            responses {
                successResponse<GroupAllowedImageType>()
            }
        }
    }
}

class GroupController(
    private val groupService: GroupService,
) {
    suspend fun handleInsertGroup(insertRequest: GroupInsertRequest) {
        groupService.saveGroup(insertRequest)
    }

    suspend fun handleDeleteGroup(id: Long) {
        groupService.deleteGroup(id)
    }

    suspend fun handleUpdateGroup(
        id: Long,
        putRequest: GroupPutRequest,
    ) {
        groupService.updateGroup(id, putRequest)
    }

    suspend fun handlePatchGroup(
        id: Long,
        patchRequest: GroupPatchRequest,
    ) {
        groupService.patchGroup(id, patchRequest)
    }

    suspend fun handleFetchGroup(id: Long): GroupVO = groupService.fetchGroup(id)

    suspend fun handleFetchGroupAllowedImageType(id: Long): GroupAllowedImageType {
        val groupVO = groupService.fetchGroup(id)
        return GroupAllowedImageType(
            allowedImageTypes =
                groupVO.groupConfig.groupStrategyConfig.allowedImageTypes
                    .toList(),
        )
    }

    suspend fun handlePageGroups(pageRequest: PageRequest): PageResult<GroupPageVO> = groupService.pageGroups(pageRequest)
}
