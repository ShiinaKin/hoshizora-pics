package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.sakurasou.constant.GROUP_DELETE
import io.sakurasou.constant.GROUP_READ_ALL
import io.sakurasou.constant.GROUP_READ_SINGLE
import io.sakurasou.constant.GROUP_WRITE
import io.sakurasou.controller.request.GroupInsertRequest
import io.sakurasou.controller.request.GroupPatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.GroupPageVO
import io.sakurasou.controller.vo.GroupVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.extension.pageRequest
import io.sakurasou.plugins.AuthorizationPlugin

/**
 * @author Shiina Kin
 * 2024/9/9 08:58
 */
fun Route.groupRoute() {
    route("group", {
        protected = true
    }) {
        groupInsert()
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
            groupDelete()
            groupUpdate()
            groupFetch()
        }
        groupPage()
    }
}

private fun Route.groupInsert() {
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
            TODO()
        }
    }
}

private fun Route.groupDelete() {
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
            TODO()
        }
    }
}

private fun Route.groupUpdate() {
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
            TODO()
        }
    }
}

private fun Route.groupFetch() {
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
            TODO()
        }
    }
}

private fun Route.groupPage() {
    route {
        install(AuthorizationPlugin) {
            permission = GROUP_READ_ALL
        }
        get("page", {
            pageRequest()
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<PageResult<GroupPageVO>> {
                        description = "page result"
                    }
                }
                HttpStatusCode.BadRequest to {
                    description = "page or pageSize wrong"
                }
            }
        }) {
            val pageVO = call.pageRequest()

            TODO()
        }
    }
}

class GroupController {
}