package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.sakurasou.constant.*
import io.sakurasou.controller.request.AlbumInsertRequest
import io.sakurasou.controller.request.AlbumPatchRequest
import io.sakurasou.controller.request.AlbumSelfPatchRequest
import io.sakurasou.controller.vo.AlbumPageVO
import io.sakurasou.controller.vo.AlbumVO
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.extension.*

/**
 * @author Shiina Kin
 * 2024/9/9 08:58
 */
fun Route.albumRoute() {
    route("album") {
        post({
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
        }, ALBUM_WRITE_SELF) {
            TODO()
        }
        route("{albumId}", {
            protected = true
            request {
                pathParameter<Long>("album id") {
                    description = "album id"
                    required = true
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
            }, ALBUM_DELETE_SELF) {
                TODO()
            }
            patch({
                request {
                    body<AlbumSelfPatchRequest> {
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "success"
                        body<CommonResponse<Unit>> { }
                    }
                }
            }, ALBUM_WRITE_SELF) {
                TODO()
            }
            get({
                response {
                    HttpStatusCode.OK to {
                        description = "success"
                        body<CommonResponse<AlbumVO>> { }
                    }
                }
            }, ALBUM_READ_SELF_SINGLE) {
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
        }, ALBUM_READ_SELF_ALL) {
            val pageVO = call.pageRequest()

            TODO()
        }
        route("all", {
            protected = true
            request {
                pathParameter<Long>("userId") {
                    description = "user id"
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
            route("{albumId}", {
                protected = true
                request {
                    pathParameter<Long>("album id") {
                        description = "album id"
                        required = true
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
                }, ALBUM_DELETE_ALL) {
                    TODO()
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
                }, ALBUM_WRITE_ALL) {
                    TODO()
                }
                get({
                    response {
                        HttpStatusCode.OK to {
                            description = "success"
                            body<CommonResponse<AlbumVO>> { }
                        }
                    }
                }, ALBUM_READ_ALL_SINGLE) {
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
            }, ALBUM_READ_ALL_ALL) {
                val pageVO = call.pageRequest()

                TODO()
            }
        }

    }
}

class AlbumController {
}