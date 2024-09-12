package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.route
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
import io.sakurasou.extension.*

/**
 * @author Shiina Kin
 * 2024/9/9 08:58
 */
fun Route.groupRoute() {
    route("group", {
        protected = true
    }) {
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
        }, GROUP_WRITE) {
            TODO()
        }
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
            delete({
                response {
                    HttpStatusCode.OK to {
                        description = "success"
                        body<CommonResponse<Unit>> { }
                    }
                }
            }, GROUP_DELETE) {
                TODO()
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
            }, GROUP_WRITE) {
                TODO()
            }
            get({
                response {
                    HttpStatusCode.OK to {
                        description = "success"
                        body<CommonResponse<GroupVO>> { }
                    }
                }
            }, GROUP_READ_SINGLE) {
                TODO()
            }
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
        }, GROUP_READ_ALL) {
            val pageVO = call.pageRequest()

            TODO()
        }
    }
}

class GroupController {
}