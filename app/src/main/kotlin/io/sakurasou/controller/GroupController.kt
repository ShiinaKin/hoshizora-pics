package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.sakurasou.constant.GROUP_DELETE
import io.sakurasou.constant.GROUP_READ_ALL
import io.sakurasou.constant.GROUP_READ_SINGLE
import io.sakurasou.constant.GROUP_WRITE
import io.sakurasou.controller.request.GroupInsertRequest
import io.sakurasou.controller.request.GroupPatchRequest
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.*
import io.sakurasou.extension.getPrincipal
import io.sakurasou.extension.id
import io.sakurasou.extension.pageRequest
import io.sakurasou.extension.success
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.group.GroupService

/**
 * @author Shiina Kin
 * 2024/9/9 08:58
 */
fun Route.groupRoute(groupService: GroupService) {
    val controller = GroupController(groupService)
    route("group", {
        protected = true
        tags("Group")
    }) {
        groupInsert(controller)
        route("{id}", {
            request {
                pathParameter<Long>("id") {
                    description = "group id"
                    required = true
                }
            }
            response {
                HttpStatusCode.NotFound to {
                    description = "group not found"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            groupDelete(controller)
            groupUpdate(controller)
            groupFetch(controller)
        }
        groupPage(controller)
        groupFetchGroupAllowedImageType(controller)
    }
}

private fun Route.groupInsert(controller: GroupController) {
    route {
        install(AuthorizationPlugin) {
            permission = GROUP_WRITE
        }
        post({
            request {
                body<GroupInsertRequest> {
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
                HttpStatusCode.Conflict to {
                    description = "group name conflict"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val insertRequest = call.receive<GroupInsertRequest>()
            controller.handleInsertGroup(insertRequest)
            call.success()
        }
    }
}

private fun Route.groupDelete(controller: GroupController) {
    route {
        install(AuthorizationPlugin) {
            permission = GROUP_DELETE
        }
        delete({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val id = call.id()
            controller.handleDeleteGroup(id)
            call.success()
        }
    }
}

private fun Route.groupUpdate(controller: GroupController) {
    route {
        install(AuthorizationPlugin) {
            permission = GROUP_WRITE
        }
        patch({
            request {
                body<GroupPatchRequest> {
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val id = call.id()
            val patchRequest = call.receive<GroupPatchRequest>()
            controller.handlePatchGroup(id, patchRequest)
            call.success()
        }
    }
}

private fun Route.groupFetch(controller: GroupController) {
    route {
        install(AuthorizationPlugin) {
            permission = GROUP_READ_SINGLE
        }
        get({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<GroupVO>> { }
                }
            }
        }) {
            val id = call.id()
            val groupVO = controller.handleFetchGroup(id)
            call.success(groupVO)
        }
    }
}

private fun Route.groupPage(controller: GroupController) {
    route {
        install(AuthorizationPlugin) {
            permission = GROUP_READ_ALL
        }
        get("page", {
            pageRequest()
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<PageResult<GroupPageVO>>> {
                        description = "page result"
                    }
                }
                HttpStatusCode.BadRequest to {
                    description = "page or pageSize wrong"
                }
            }
        }) {
            val pageRequest = call.pageRequest()
            val pageResult = controller.handlePageGroups(pageRequest)
            call.success(pageResult)
        }
    }
}

private fun Route.groupFetchGroupAllowedImageType(controller: GroupController) {
    route {
        install(AuthorizationPlugin) {
            permission = GROUP_READ_SINGLE
        }
        get("type", {
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<GroupAllowedImageType>> { }
                }
            }
        }) {
            val groupId = call.getPrincipal().groupId
            val groupVO = controller.handleFetchGroupAllowedImageType(groupId)
            call.success(groupVO)
        }
    }
}

class GroupController(
    private val groupService: GroupService
) {
    suspend fun handleInsertGroup(insertRequest: GroupInsertRequest) {
        groupService.saveGroup(insertRequest)
    }

    suspend fun handleDeleteGroup(id: Long) {
        groupService.deleteGroup(id)
    }

    suspend fun handlePatchGroup(id: Long, patchRequest: GroupPatchRequest) {
        groupService.updateGroup(id, patchRequest)
    }

    suspend fun handleFetchGroup(id: Long): GroupVO {
        return groupService.fetchGroup(id)
    }

    suspend fun handleFetchGroupAllowedImageType(id: Long): GroupAllowedImageType {
        val groupVO = groupService.fetchGroup(id)
        return GroupAllowedImageType(
            allowedImageTypes = groupVO.groupConfig.groupStrategyConfig.allowedImageTypes.map { it.name }.toSet()
        )
    }

    suspend fun handlePageGroups(pageRequest: PageRequest): PageResult<GroupPageVO> {
        return groupService.pageGroups(pageRequest)
    }
}