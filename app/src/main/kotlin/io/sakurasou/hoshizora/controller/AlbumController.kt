@file:OptIn(ExperimentalKtorApi::class)

package io.sakurasou.hoshizora.controller

import io.ktor.http.HttpStatusCode
import io.ktor.openapi.jsonSchema
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi
import io.sakurasou.hoshizora.constant.ALBUM_DELETE_ALL
import io.sakurasou.hoshizora.constant.ALBUM_DELETE_SELF
import io.sakurasou.hoshizora.constant.ALBUM_READ_ALL_ALL
import io.sakurasou.hoshizora.constant.ALBUM_READ_ALL_SINGLE
import io.sakurasou.hoshizora.constant.ALBUM_READ_SELF_ALL
import io.sakurasou.hoshizora.constant.ALBUM_READ_SELF_SINGLE
import io.sakurasou.hoshizora.constant.ALBUM_WRITE_ALL
import io.sakurasou.hoshizora.constant.ALBUM_WRITE_SELF
import io.sakurasou.hoshizora.controller.request.AlbumManageInsertRequest
import io.sakurasou.hoshizora.controller.request.AlbumManagePatchRequest
import io.sakurasou.hoshizora.controller.request.AlbumSelfInsertRequest
import io.sakurasou.hoshizora.controller.request.AlbumSelfPatchRequest
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.AlbumManagePageVO
import io.sakurasou.hoshizora.controller.vo.AlbumManageVO
import io.sakurasou.hoshizora.controller.vo.AlbumPageVO
import io.sakurasou.hoshizora.controller.vo.AlbumVO
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.exception.controller.param.WrongParameterException
import io.sakurasou.hoshizora.extension.commonResponse
import io.sakurasou.hoshizora.extension.getPrincipal
import io.sakurasou.hoshizora.extension.pageRequest
import io.sakurasou.hoshizora.extension.pageRequestSpec
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.extension.successResponse
import io.sakurasou.hoshizora.extension.transparentRoute
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin
import io.sakurasou.hoshizora.service.album.AlbumService

/**
 * @author Shiina Kin
 * 2024/9/9 08:58
 */
fun Route.albumRoute(albumService: AlbumService) {
    val controller = AlbumController(albumService)
    route("album") {
        albumSelfRoute(controller)
        albumManageRoute(controller)
    }.describe {
        tag("Album")
    }
}

private fun Route.albumSelfRoute(controller: AlbumController) {
    albumSelfInsert(controller)
    route("{albumId}") {
        albumSelfDelete(controller)
        albumSelfPatch(controller)
        albumSelfFetch(controller)
    }.describe {
        parameters {
            path("albumId") {
                description = "album id"
                required = true
                schema = jsonSchema<Long>()
            }
        }
    }
    albumSelfPage(controller)
}

private fun Route.albumSelfInsert(controller: AlbumController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ALBUM_WRITE_SELF
        }
        install(RequestValidation) {
            validate<AlbumSelfInsertRequest> { selfInsertRequest ->
                if (selfInsertRequest.name.isBlank()) {
                    ValidationResult.Invalid("albumName is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        post {
            val userId = call.getPrincipal().id
            val selfInsertRequest = call.receive<AlbumSelfInsertRequest>()
            controller.handleSelfInsert(userId, selfInsertRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                schema = jsonSchema<AlbumSelfInsertRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.albumSelfDelete(controller: AlbumController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ALBUM_DELETE_SELF
        }
        delete {
            val userId = call.getPrincipal().id
            val albumId = call.albumId()
            controller.handleSelfDelete(userId, albumId)
            call.success()
        }.describe {
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.albumSelfPatch(controller: AlbumController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ALBUM_WRITE_SELF
        }
        install(RequestValidation) {
            validate<AlbumSelfPatchRequest> { selfPatchRequest ->
                if (selfPatchRequest.name == null &&
                    selfPatchRequest.description == null &&
                    selfPatchRequest.isDefault == null
                ) {
                    ValidationResult.Invalid("at least one field is required")
                } else if (selfPatchRequest.name?.isBlank() == true) {
                    ValidationResult.Invalid("albumName is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch {
            val userId = call.getPrincipal().id
            val albumId = call.albumId()
            val selfPatchRequest = call.receive<AlbumSelfPatchRequest>()
            controller.handleSelfPatch(userId, albumId, selfPatchRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                schema = jsonSchema<AlbumSelfPatchRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.albumSelfFetch(controller: AlbumController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ALBUM_READ_SELF_SINGLE
        }
        get {
            val userId = call.getPrincipal().id
            val albumId = call.albumId()
            val albumVO = controller.handleSelfFetch(userId, albumId)
            call.success(albumVO)
        }.describe {
            responses {
                successResponse<AlbumVO>()
            }
        }
    }
}

private fun Route.albumSelfPage(controller: AlbumController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ALBUM_READ_SELF_ALL
        }
        get("page") {
            val userId = call.getPrincipal().id
            val pageRequest = call.pageRequest()
            val albumNamePair = call.parameters["albumName"]?.let { "albumName" to it }
            pageRequest.additionalCondition =
                mutableMapOf<String, String>().apply {
                    albumNamePair?.let { put(it.first, it.second) }
                }

            val pageResult = controller.handleSelfPage(userId, pageRequest)
            call.success(pageResult)
        }.describe {
            pageRequestSpec()
            parameters {
                query("albumName") {
                    description = "search albumName"
                    required = false
                    schema = jsonSchema<String>()
                }
            }
            responses {
                successResponse<PageResult<AlbumPageVO>>("page result")
                HttpStatusCode.BadRequest {
                    description = "page or pageSize wrong"
                }
            }
        }
    }
}

private fun Route.albumManageRoute(controller: AlbumController) {
    route("manage") {
        albumManageInsert(controller)
        route("{albumId}") {
            albumManageDelete(controller)
            albumManagePatch(controller)
            albumManageFetch(controller)
        }.describe {
            parameters {
                path("albumId") {
                    description = "album id"
                    required = true
                    schema = jsonSchema<Long>()
                }
            }
            responses {
                commonResponse(HttpStatusCode.NotFound, "album not found")
            }
        }
        albumManagePage(controller)
    }
}

private fun Route.albumManageInsert(controller: AlbumController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ALBUM_WRITE_ALL
        }
        install(RequestValidation) {
            validate<AlbumManageInsertRequest> { manageInsertRequest ->
                if (manageInsertRequest.name.isBlank()) {
                    ValidationResult.Invalid("albumName is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        post {
            val manageInsertRequest = call.receive<AlbumManageInsertRequest>()
            controller.handleManageInsert(manageInsertRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                schema = jsonSchema<AlbumManageInsertRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.albumManageDelete(controller: AlbumController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ALBUM_DELETE_ALL
        }
        delete {
            val albumId = call.albumId()
            controller.handleManageDelete(albumId)
            call.success()
        }.describe {
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.albumManagePatch(controller: AlbumController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ALBUM_WRITE_ALL
        }
        install(RequestValidation) {
            validate<AlbumManagePatchRequest> { managePatchRequest ->
                if (managePatchRequest.name == null &&
                    managePatchRequest.description == null &&
                    managePatchRequest.isDefault == null &&
                    managePatchRequest.userId == null
                ) {
                    ValidationResult.Invalid("at least one field is required")
                } else if (managePatchRequest.name?.isBlank() == true) {
                    ValidationResult.Invalid("albumName is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch {
            val albumId = call.albumId()
            val managePatchRequest = call.receive<AlbumManagePatchRequest>()
            controller.handleManagePatch(albumId, managePatchRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                schema = jsonSchema<AlbumManagePatchRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.albumManageFetch(controller: AlbumController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ALBUM_READ_ALL_SINGLE
        }
        get {
            val albumId = call.albumId()
            val albumVO = controller.handleManageFetch(albumId)
            call.success(albumVO)
        }.describe {
            responses {
                successResponse<AlbumManageVO>()
            }
        }
    }
}

private fun Route.albumManagePage(controller: AlbumController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = ALBUM_READ_ALL_ALL
        }
        get("page") {
            val pageRequest = call.pageRequest()
            val userIdPair = call.parameters["userId"]?.toLongOrNull()?.let { "userId" to it.toString() }
            val albumNamePair = call.parameters["albumName"]?.let { "albumName" to it }
            pageRequest.additionalCondition =
                mutableMapOf<String, String>().apply {
                    userIdPair?.let { put(it.first, it.second) }
                    albumNamePair?.let { put(it.first, it.second) }
                }

            val pageResult = controller.handleManagePage(pageRequest)
            call.success(pageResult)
        }.describe {
            pageRequestSpec()
            parameters {
                query("userId") {
                    description = "userId"
                    required = false
                    schema = jsonSchema<Long>()
                }
                query("albumName") {
                    description = "search albumName"
                    required = false
                    schema = jsonSchema<String>()
                }
            }
            responses {
                successResponse<PageResult<AlbumManagePageVO>>("page result")
                HttpStatusCode.BadRequest {
                    description = "page or pageSize wrong"
                }
            }
        }
    }
}

private fun ApplicationCall.albumId() = parameters["albumId"]?.toLongOrNull() ?: throw WrongParameterException()

class AlbumController(
    private val albumService: AlbumService,
) {
    suspend fun handleSelfInsert(
        userId: Long,
        selfInsertRequest: AlbumSelfInsertRequest,
    ) {
        albumService.saveSelf(userId, selfInsertRequest)
    }

    suspend fun handleSelfPatch(
        userId: Long,
        albumId: Long,
        selfPatchRequest: AlbumSelfPatchRequest,
    ) {
        albumService.patchSelf(userId, albumId, selfPatchRequest)
    }

    suspend fun handleSelfDelete(
        userId: Long,
        albumId: Long,
    ) {
        albumService.deleteSelf(userId, albumId)
    }

    suspend fun handleSelfFetch(
        userId: Long,
        albumId: Long,
    ): AlbumVO {
        val albumVO = albumService.fetchSelf(userId, albumId)
        return albumVO
    }

    suspend fun handleSelfPage(
        userId: Long,
        pageRequest: PageRequest,
    ): PageResult<AlbumPageVO> {
        val pageResult = albumService.pageSelf(userId, pageRequest)
        return pageResult
    }

    suspend fun handleManageInsert(manageInsertRequest: AlbumManageInsertRequest) {
        albumService.saveAlbum(manageInsertRequest)
    }

    suspend fun handleManagePatch(
        albumId: Long,
        managePatchRequest: AlbumManagePatchRequest,
    ) {
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
