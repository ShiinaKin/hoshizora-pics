package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.sakurasou.constant.*
import io.sakurasou.controller.request.*
import io.sakurasou.controller.vo.AlbumManagePageVO
import io.sakurasou.controller.vo.AlbumManageVO
import io.sakurasou.controller.vo.AlbumPageVO
import io.sakurasou.controller.vo.AlbumVO
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.exception.controller.param.WrongParameterException
import io.sakurasou.extension.getPrincipal
import io.sakurasou.extension.pageRequest
import io.sakurasou.extension.pageRequestSpec
import io.sakurasou.extension.success
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.album.AlbumService

/**
 * @author Shiina Kin
 * 2024/9/9 08:58
 */
fun Route.albumRoute(albumService: AlbumService) {
    val controller = AlbumController(albumService)
    route("album", {
        tags("Album")
    }) {
        albumSelfRoute(controller)
        albumManageRoute(controller)
    }
}

private fun Route.albumSelfRoute(controller: AlbumController) {
    route({
        protected = true
    }) {
        albumSelfInsert(controller)
        route("{albumId}", {
            request {
                pathParameter<Long>("albumId") {
                    description = "album id"
                    required = true
                }
            }
        }) {
            albumSelfDelete(controller)
            albumSelfUpdate(controller)
            albumSelfFetch(controller)
        }
        albumSelfPage(controller)
    }
}

private fun Route.albumSelfInsert(controller: AlbumController) {
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
            val userId = call.getPrincipal().id
            val selfInsertRequest = call.receive<AlbumSelfInsertRequest>()
            controller.handleSelfInsert(userId, selfInsertRequest)
            call.success()
        }
    }
}

private fun Route.albumSelfDelete(controller: AlbumController) {
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
            val userId = call.getPrincipal().id
            val albumId = call.albumId()
            controller.handleSelfDelete(userId, albumId)
            call.success()
        }
    }
}

private fun Route.albumSelfUpdate(controller: AlbumController) {
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
            val userId = call.getPrincipal().id
            val albumId = call.albumId()
            val selfPatchRequest = call.receive<AlbumSelfPatchRequest>()
            controller.handleSelfPatch(userId, albumId, selfPatchRequest)
            call.success()
        }
    }
}

private fun Route.albumSelfFetch(controller: AlbumController) {
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
            val userId = call.getPrincipal().id
            val albumId = call.albumId()
            val albumVO = controller.handleSelfFetch(userId, albumId)
            call.success(albumVO)
        }
    }
}

private fun Route.albumSelfPage(controller: AlbumController) {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_READ_SELF_ALL
        }
        get("page", {
            pageRequestSpec()
            request {
                queryParameter<String>("albumName") {
                    description = "search albumName"
                    required = false
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<PageResult<AlbumPageVO>>> {
                        description = "page result"
                    }
                }
                HttpStatusCode.BadRequest to {
                    description = "page or pageSize wrong"
                }
            }
        }) {
            val userId = call.getPrincipal().id
            val pageRequest = call.pageRequest()
            val albumNamePair = call.parameters["albumName"]?.let { "albumName" to it }
            pageRequest.additionalCondition = mutableMapOf<String, String>().apply {
                albumNamePair?.let { put(it.first, it.second) }
            }

            val pageResult = controller.handleSelfPage(userId, pageRequest)
            call.success(pageResult)
        }
    }
}

private fun Route.albumManageRoute(controller: AlbumController) {
    route("manage", {
        protected = true
        response {
            HttpStatusCode.NotFound to {
                description = "album not found"
                body<CommonResponse<Unit>> { }
            }
        }
    }) {
        albumManageInsert(controller)
        route("{albumId}", {
            protected = true
            request {
                pathParameter<Long>("albumId") {
                    description = "album id"
                    required = true
                }
            }
        }) {
            albumManageDelete(controller)
            albumManageUpdate(controller)
            albumManageFetch(controller)
        }
        albumManagePage(controller)
    }
}

private fun Route.albumManageInsert(controller: AlbumController) {
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
            val manageInsertRequest = call.receive<AlbumManageInsertRequest>()
            controller.handleManageInsert(manageInsertRequest)
            call.success()
        }
    }
}

private fun Route.albumManageDelete(controller: AlbumController) {
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
            val albumId = call.albumId()
            controller.handleManageDelete(albumId)
            call.success()
        }
    }
}

private fun Route.albumManageUpdate(controller: AlbumController) {
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
            val albumId = call.albumId()
            val managePatchRequest = call.receive<AlbumManagePatchRequest>()
            controller.handleManagePatch(albumId, managePatchRequest)
            call.success()
        }
    }
}

private fun Route.albumManageFetch(controller: AlbumController) {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_READ_ALL_SINGLE
        }
        get({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<AlbumManageVO>> { }
                }
            }
        }) {
            val albumId = call.albumId()
            val albumVO = controller.handleManageFetch(albumId)
            call.success(albumVO)
        }
    }
}

private fun Route.albumManagePage(controller: AlbumController) {
    route {
        install(AuthorizationPlugin) {
            permission = ALBUM_READ_ALL_ALL
        }
        get("page", {
            pageRequestSpec()
            request {
                queryParameter<Long>("userId") {
                    description = "userId"
                    required = false
                }
                queryParameter<String>("albumName") {
                    description = "search albumName"
                    required = false
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<PageResult<AlbumManagePageVO>>> {
                        description = "page result"
                    }
                }
                HttpStatusCode.BadRequest to {
                    description = "page or pageSize wrong"
                }
            }
        }) {
            val pageRequest = call.pageRequest()
            val userIdPair = call.parameters["userId"]?.toLongOrNull()?.let { "userId" to it.toString() }
            val albumNamePair = call.parameters["albumName"]?.let { "albumName" to it }
            pageRequest.additionalCondition = mutableMapOf<String, String>().apply {
                userIdPair?.let { put(it.first, it.second) }
                albumNamePair?.let { put(it.first, it.second) }
            }

            val pageResult = controller.handleManagePage(pageRequest)
            call.success(pageResult)
        }
    }
}

private fun ApplicationCall.albumId() = parameters["albumId"]?.toLongOrNull() ?: throw WrongParameterException()

class AlbumController(
    private val albumService: AlbumService
) {
    suspend fun handleSelfInsert(userId: Long, selfInsertRequest: AlbumSelfInsertRequest) {
        albumService.saveSelf(userId, selfInsertRequest)
    }

    suspend fun handleSelfPatch(userId: Long, albumId: Long, selfPatchRequest: AlbumSelfPatchRequest) {
        albumService.patchSelf(userId, albumId, selfPatchRequest)
    }

    suspend fun handleSelfDelete(userId: Long, albumId: Long) {
        albumService.deleteSelf(userId, albumId)
    }

    suspend fun handleSelfFetch(userId: Long, albumId: Long): AlbumVO {
        val albumVO = albumService.fetchSelf(userId, albumId)
        return albumVO
    }

    suspend fun handleSelfPage(userId: Long, pageRequest: PageRequest): PageResult<AlbumPageVO> {
        val pageResult = albumService.pageSelf(userId, pageRequest)
        return pageResult
    }

    suspend fun handleManageInsert(manageInsertRequest: AlbumManageInsertRequest) {
        albumService.saveAlbum(manageInsertRequest)
    }

    suspend fun handleManagePatch(albumId: Long, managePatchRequest: AlbumManagePatchRequest) {
        albumService.patchAlbum(albumId, managePatchRequest)
    }

    suspend fun handleManageDelete(albumId: Long) {
        albumService.deleteAlbum(albumId)
    }

    suspend fun handleManageFetch(albumId: Long): AlbumManageVO {
        val albumVO = albumService.fetchAlbum(albumId)
        return albumVO
    }

    suspend fun handleManagePage(pageRequest: PageRequest): PageResult<AlbumManagePageVO> {
        val pageResult = albumService.pageAlbum(pageRequest)
        return pageResult
    }
}