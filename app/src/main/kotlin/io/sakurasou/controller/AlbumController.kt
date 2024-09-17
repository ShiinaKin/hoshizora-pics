package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.sakurasou.constant.*
import io.sakurasou.controller.request.AlbumManageInsertRequest
import io.sakurasou.controller.request.AlbumManagePatchRequest
import io.sakurasou.controller.request.AlbumSelfInsertRequest
import io.sakurasou.controller.request.AlbumSelfPatchRequest
import io.sakurasou.controller.vo.AlbumPageVO
import io.sakurasou.controller.vo.AlbumVO
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.extension.pageRequest
import io.sakurasou.plugins.AuthorizationPlugin

/**
 * @author Shiina Kin
 * 2024/9/9 08:58
 */
fun Route.albumRoute() {
    route("album") {
        albumSelfRoute()
        albumManageRoute()
    }
}

private fun Route.albumSelfRoute() {
    route({
        protected = true
    }) {
        albumSelfInsert()
        route("{albumId}", {
            request {
                pathParameter<Long>("album id") {
                    description = "album id"
                    required = true
                }
            }
        }) {
            albumSelfDelete()
            albumSelfUpdate()
            albumSelfFetch()
        }
        albumSelfPage()
    }
}

private fun Route.albumSelfInsert() {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_WRITE_SELF
        }
        post({
            request {
                body<AlbumSelfInsertRequest> {
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

private fun Route.albumSelfDelete() {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_DELETE_SELF
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

private fun Route.albumSelfUpdate() {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_WRITE_SELF
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
        }) {
            TODO()
        }
    }
}

private fun Route.albumSelfFetch() {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_READ_SELF_SINGLE
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
}

private fun Route.albumSelfPage() {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_READ_SELF_ALL
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

private fun Route.albumManageRoute() {
    route("manage", {
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
        albumManageInsert()
        route("{albumId}", {
            protected = true
            request {
                pathParameter<Long>("album id") {
                    description = "album id"
                    required = true
                }
            }
        }) {
            albumManageDelete()
            albumManageUpdate()
            albumManageFetch()
        }
        albumManagePage()
    }
}

private fun Route.albumManageInsert() {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_WRITE_ALL
        }
        post({
            request {
                body<AlbumManageInsertRequest> {
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

private fun Route.albumManageDelete() {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_DELETE_ALL
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

private fun Route.albumManageUpdate() {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_WRITE_ALL
        }
        patch({
            request {
                body<AlbumManagePatchRequest> {
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

private fun Route.albumManageFetch() {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_READ_ALL_SINGLE
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
}

private fun Route.albumManagePage() {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_READ_ALL_ALL
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