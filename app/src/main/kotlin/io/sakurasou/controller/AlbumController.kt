package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.sakurasou.controller.request.AlbumInsertRequest
import io.sakurasou.controller.request.AlbumPatchRequest
import io.sakurasou.controller.request.pageRequest
import io.sakurasou.controller.vo.AlbumPageVO
import io.sakurasou.controller.vo.AlbumVO
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PageResult

/**
 * @author Shiina Kin
 * 2024/9/9 08:58
 */
fun Route.albumRoute() {
    route("album") {
        post({
            protected = true
            request {
                body<AlbumInsertRequest> {
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
        route("{id}", {
            protected = true
            request {
                pathParameter<Long>("id") {
                    description = "album id"
                    required = true
                }
            }
            response {
                HttpStatusCode.NotFound to {
                    description = "album not found"
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
            }) {

            }
            patch({
                request {
                    body<AlbumPatchRequest> {
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
            get({
                response {
                    HttpStatusCode.OK to {
                        description = "success"
                        body<CommonResponse<AlbumVO>> { }
                    }
                }
            }) {
                TODO()
            }
        }
        get("page", {
            pageRequest()
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<PageResult<AlbumPageVO>> {
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

class AlbumController {
}