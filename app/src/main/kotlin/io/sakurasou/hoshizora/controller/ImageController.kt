@file:OptIn(ExperimentalKtorApi::class)

package io.sakurasou.hoshizora.controller

import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.openapi.jsonSchema
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi
import io.ktor.utils.io.readRemaining
import io.sakurasou.hoshizora.constant.IMAGE_DELETE_ALL
import io.sakurasou.hoshizora.constant.IMAGE_DELETE_SELF
import io.sakurasou.hoshizora.constant.IMAGE_READ_ALL_ALL
import io.sakurasou.hoshizora.constant.IMAGE_READ_ALL_SINGLE
import io.sakurasou.hoshizora.constant.IMAGE_READ_SELF_ALL
import io.sakurasou.hoshizora.constant.IMAGE_READ_SELF_SINGLE
import io.sakurasou.hoshizora.constant.IMAGE_WRITE_ALL
import io.sakurasou.hoshizora.constant.IMAGE_WRITE_SELF
import io.sakurasou.hoshizora.controller.request.ImageManagePatchRequest
import io.sakurasou.hoshizora.controller.request.ImagePatchRequest
import io.sakurasou.hoshizora.controller.request.ImageRawFile
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.ImageManagePageVO
import io.sakurasou.hoshizora.controller.vo.ImageManageVO
import io.sakurasou.hoshizora.controller.vo.ImagePageVO
import io.sakurasou.hoshizora.controller.vo.ImageVO
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.di.InstanceCenter.client
import io.sakurasou.hoshizora.exception.controller.param.UnsupportedFileTypeException
import io.sakurasou.hoshizora.exception.controller.param.WrongParameterException
import io.sakurasou.hoshizora.extension.commonResponse
import io.sakurasou.hoshizora.extension.getPrincipal
import io.sakurasou.hoshizora.extension.pageRequest
import io.sakurasou.hoshizora.extension.pageRequestSpec
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.extension.successResponse
import io.sakurasou.hoshizora.extension.transparentRoute
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin
import io.sakurasou.hoshizora.plugins.cache
import io.sakurasou.hoshizora.service.image.ImageService
import kotlinx.io.readByteArray
import io.ktor.client.request.get as clientGet

/**
 * @author ShiinaKin
 * 2024/9/5 15:17
 */
fun Route.imageRoute(imageService: ImageService) {
    val controller = ImageController(imageService)
    route("image") {
        imageSelfRoute(controller)
        imageManageRoute(controller)
    }.describe {
        tag("Image")
    }
}

private fun Route.imageSelfRoute(controller: ImageController) {
    imageSelfUpload(controller)
    route("{imageId}") {
        imageSelfDelete(controller)
        imageSelfPatch(controller)
        imageSelfInfoFetch(controller)
        cache { imageSelfFileFetch(controller) }
        cache { imageSelfThumbnailFileFetch(controller) }
    }.describe {
        parameters {
            path("imageId") {
                description = "image id"
                required = true
                schema = jsonSchema<Long>()
            }
        }
    }
    imageSelfPage(controller)
}

private fun Route.imageSelfUpload(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_WRITE_SELF
        }
        post {
            val userId = call.getPrincipal().id

            val multipartData = call.receiveMultipart()
            var imageRawFile: ImageRawFile? = null
            var isMoreThanOne = -1

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        if (isMoreThanOne >= 0) {
                            throw WrongParameterException("Only one file is allowed")
                        }
                        if (part.contentType?.match(ContentType.Image.Any) == false) {
                            throw UnsupportedFileTypeException()
                        }
                        val fileName = part.originalFileName as String
                        val mimeType = part.contentType!!.toString()
                        val bytes = part.provider().readRemaining().readByteArray()
                        val contentLength = bytes.size.toLong()

                        imageRawFile =
                            ImageRawFile(
                                name = fileName,
                                mimeType = mimeType,
                                size = contentLength,
                                bytes = bytes,
                            )

                        isMoreThanOne++
                    }

                    else -> {}
                }
                part.dispose()
            }

            if (isMoreThanOne < 0 || imageRawFile == null) {
                throw WrongParameterException("No file found")
            }

            val imageName = controller.handleSelfUpload(userId, imageRawFile)
            call.success(imageName)
        }.describe {
            requestBody {
                required = true
                description = "multipart/form-data with a single image file"
            }
            responses {
                successResponse<String>("success, return image external id, or empty string if it is private")
            }
        }
    }
}

private fun Route.imageSelfDelete(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_DELETE_SELF
        }
        delete {
            val userId = call.getPrincipal().id
            val imageId = call.imageId()
            controller.handleSelfDelete(userId, imageId)
            call.success()
        }.describe {
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.imageSelfPatch(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_WRITE_SELF
        }
        install(RequestValidation) {
            validate<ImagePatchRequest> { patchRequest ->
                if (patchRequest.albumId == null &&
                    patchRequest.displayName == null &&
                    patchRequest.description == null &&
                    patchRequest.isPrivate == null &&
                    patchRequest.isAllowedRandomFetch == null
                ) {
                    ValidationResult.Invalid("at least one field should be provided")
                } else if (patchRequest.displayName != null && patchRequest.displayName.isBlank()) {
                    ValidationResult.Invalid("displayName is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch {
            val userId = call.getPrincipal().id
            val imageId = call.imageId()
            val imagePatchRequest = call.receive<ImagePatchRequest>()
            controller.handleSelfUpdate(userId, imageId, imagePatchRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                schema = jsonSchema<ImagePatchRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.imageSelfInfoFetch(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_SELF_SINGLE
        }
        get("info") {
            val userId = call.getPrincipal().id
            val imageId = call.imageId()
            val imageVO = controller.handleSelfFetch(userId, imageId)
            call.success(imageVO)
        }.describe {
            responses {
                successResponse<ImageVO>()
            }
        }
    }
}

private fun Route.imageSelfFileFetch(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_SELF_SINGLE
        }
        get("file") {
            val userId = call.getPrincipal().id
            val imageId = call.imageId()
            val imageFileDTO = controller.handleSelfFileFetch(userId, imageId)
            if (imageFileDTO.bytes != null) {
                call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            } else {
                call.respondBytes(client.clientGet(imageFileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
            }
        }.describe {
            responses {
                HttpStatusCode.OK {
                    description = "success"
                    ContentType.Image.Any {
                        schema = jsonSchema<ByteArray>()
                    }
                }
            }
        }
    }
}

private fun Route.imageSelfThumbnailFileFetch(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_SELF_SINGLE
        }
        get("thumbnail") {
            val userId = call.getPrincipal().id
            val imageId = call.imageId()
            val imageFileDTO = controller.handleSelfThumbnailFileFetch(userId, imageId)
            if (imageFileDTO.bytes != null) {
                call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            } else {
                call.respondBytes(client.clientGet(imageFileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
            }
        }.describe {
            responses {
                HttpStatusCode.OK {
                    description = "success"
                    ContentType.Image.Any {
                        schema = jsonSchema<ByteArray>()
                    }
                }
            }
        }
    }
}

private fun Route.imageSelfPage(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_SELF_ALL
        }
        get("page") {
            val userId = call.getPrincipal().id
            val pageRequest = call.pageRequest()
            val albumIdPair = call.parameters["albumId"]?.toLongOrNull()?.let { "albumId" to it.toString() }
            val isPrivatePair = call.parameters["isPrivate"]?.toBoolean()?.let { "isPrivate" to it.toString() }
            val searchPair = call.parameters["search"]?.let { "search" to it }
            pageRequest.additionalCondition =
                mutableMapOf<String, String>().apply {
                    albumIdPair?.let { put(it.first, it.second) }
                    isPrivatePair?.let { put(it.first, it.second) }
                    searchPair?.let { put(it.first, it.second) }
                }

            val pageResult = controller.handleSelfPage(userId, pageRequest)
            call.success(pageResult)
        }.describe {
            pageRequestSpec()
            parameters {
                query("albumId") {
                    description = "albumId"
                    required = false
                    schema = jsonSchema<Long>()
                }
                query("isPrivate") {
                    description = "isPrivate"
                    required = false
                    schema = jsonSchema<Boolean>()
                }
                query("search") {
                    description = "search content"
                    required = false
                    schema = jsonSchema<String>()
                }
            }
            responses {
                successResponse<PageResult<ImagePageVO>>("page result")
                HttpStatusCode.BadRequest {
                    description = "page or pageSize wrong"
                }
            }
        }
    }
}

private fun Route.imageManageRoute(controller: ImageController) {
    route("manage") {
        route("{imageId}") {
            imageManageDelete(controller)
            imageManagePatch(controller)
            imageManageInfoFetch(controller)
            cache { imageManageFileFetch(controller) }
            cache { imageManageThumbnailFileFetch(controller) }
        }.describe {
            parameters {
                path("imageId") {
                    description = "image id"
                    required = true
                    schema = jsonSchema<Long>()
                }
            }
            responses {
                commonResponse(HttpStatusCode.NotFound, "image not found")
            }
        }
        imageManagePage(controller)
    }
}

private fun Route.imageManageDelete(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_DELETE_ALL
        }
        delete {
            val imageId = call.imageId()
            controller.handleManageDelete(imageId)
            call.success()
        }.describe {
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.imageManagePatch(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_WRITE_ALL
        }
        install(RequestValidation) {
            validate<ImageManagePatchRequest> { managePatchRequest ->
                if (managePatchRequest.userId == null &&
                    managePatchRequest.albumId == null &&
                    managePatchRequest.displayName == null &&
                    managePatchRequest.description == null &&
                    managePatchRequest.isPrivate == null &&
                    managePatchRequest.isAllowedRandomFetch == null
                ) {
                    ValidationResult.Invalid("at least one field should be provided")
                } else if (managePatchRequest.displayName != null && managePatchRequest.displayName.isBlank()) {
                    ValidationResult.Invalid("displayName is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch {
            val imageId = call.imageId()
            val imagePatchRequest = call.receive<ImageManagePatchRequest>()
            controller.handleManageUpdate(imageId, imagePatchRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                schema = jsonSchema<ImageManagePatchRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.imageManageInfoFetch(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_ALL_SINGLE
        }
        get("info") {
            val imageId = call.imageId()
            val imageManageVO = controller.handleManageFetch(imageId)
            call.success(imageManageVO)
        }.describe {
            responses {
                successResponse<ImageManageVO>()
            }
        }
    }
}

private fun Route.imageManageFileFetch(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_ALL_SINGLE
        }
        get("file") {
            val imageId = call.imageId()
            val imageFileDTO = controller.handleManageFileFetch(imageId)
            if (imageFileDTO.bytes != null) {
                call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            } else {
                call.respondBytes(client.clientGet(imageFileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
            }
        }.describe {
            responses {
                HttpStatusCode.OK {
                    description = "success"
                    ContentType.Image.Any {
                        schema = jsonSchema<ByteArray>()
                    }
                }
            }
        }
    }
}

private fun Route.imageManageThumbnailFileFetch(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_ALL_SINGLE
        }
        get("thumbnail") {
            val imageId = call.imageId()
            val imageFileDTO = controller.handleManageThumbnailFileFetch(imageId)
            if (imageFileDTO.bytes != null) {
                call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            } else {
                call.respondBytes(client.clientGet(imageFileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
            }
        }.describe {
            responses {
                HttpStatusCode.OK {
                    description = "success"
                    ContentType.Image.Any {
                        schema = jsonSchema<ByteArray>()
                    }
                }
            }
        }
    }
}

private fun Route.imageManagePage(controller: ImageController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_ALL_ALL
        }
        get("page") {
            val pageRequest = call.pageRequest()
            val userIdPair = call.parameters["userId"]?.toLongOrNull()?.let { "userId" to it.toString() }
            val albumIdPair = call.parameters["albumId"]?.toLongOrNull()?.let { "albumId" to it.toString() }
            val isPrivatePair = call.parameters["isPrivate"]?.toBoolean()?.let { "isPrivate" to it.toString() }
            val searchPair = call.parameters["search"]?.let { "search" to it }
            pageRequest.additionalCondition =
                mutableMapOf<String, String>().apply {
                    userIdPair?.let { put(it.first, it.second) }
                    albumIdPair?.let { put(it.first, it.second) }
                    isPrivatePair?.let { put(it.first, it.second) }
                    searchPair?.let { put(it.first, it.second) }
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
                query("albumId") {
                    description = "albumId"
                    required = false
                    schema = jsonSchema<Long>()
                }
                query("isPrivate") {
                    description = "isPrivate"
                    required = false
                    schema = jsonSchema<Boolean>()
                }
                query("search") {
                    description = "search content"
                    required = false
                    schema = jsonSchema<String>()
                }
            }
            responses {
                successResponse<PageResult<ImageManagePageVO>>("page result")
                HttpStatusCode.BadRequest {
                    description = "page or pageSize wrong"
                }
            }
        }
    }
}

private fun ApplicationCall.imageId() = parameters["imageId"]?.toLongOrNull() ?: throw IllegalArgumentException("imageId")

class ImageController(
    private val imageService: ImageService,
) {
    suspend fun handleSelfUpload(
        userId: Long,
        imageRawFile: ImageRawFile,
    ): String {
        val imageName = imageService.saveImage(userId, imageRawFile)
        return imageName
    }

    suspend fun handleSelfDelete(
        userId: Long,
        imageId: Long,
    ) {
        imageService.deleteSelfImage(userId, imageId)
    }

    suspend fun handleSelfUpdate(
        userId: Long,
        imageId: Long,
        imagePatchRequest: ImagePatchRequest,
    ) {
        imageService.patchSelfImage(userId, imageId, imagePatchRequest)
    }

    suspend fun handleSelfFetch(
        userId: Long,
        imageId: Long,
    ): ImageVO {
        val imageVO = imageService.fetchSelfImageInfo(userId, imageId)
        return imageVO
    }

    suspend fun handleSelfFileFetch(
        userId: Long,
        imageId: Long,
    ): io.sakurasou.hoshizora.model.dto.ImageFileDTO {
        val imageFileDTO = imageService.fetchSelfImageFile(userId, imageId)
        return imageFileDTO
    }

    suspend fun handleSelfThumbnailFileFetch(
        userId: Long,
        imageId: Long,
    ): io.sakurasou.hoshizora.model.dto.ImageFileDTO {
        val imageFileDTO = imageService.fetchSelfImageThumbnailFile(userId, imageId)
        return imageFileDTO
    }

    suspend fun handleSelfPage(
        userId: Long,
        pageRequest: PageRequest,
    ): PageResult<ImagePageVO> {
        val pageResult = imageService.pageSelfImage(userId, pageRequest)
        return pageResult
    }

    suspend fun handleManageDelete(imageId: Long) {
        imageService.deleteImage(imageId)
    }

    suspend fun handleManageUpdate(
        imageId: Long,
        imagePatchRequest: ImageManagePatchRequest,
    ) {
        imageService.patchImage(imageId, imagePatchRequest)
    }

    suspend fun handleManageFetch(imageId: Long): ImageManageVO {
        val imageManageVO = imageService.fetchImageInfo(imageId)
        return imageManageVO
    }

    suspend fun handleManageFileFetch(imageId: Long): io.sakurasou.hoshizora.model.dto.ImageFileDTO {
        val imageFileDTO = imageService.fetchImageFile(imageId)
        return imageFileDTO
    }

    suspend fun handleManageThumbnailFileFetch(imageId: Long): io.sakurasou.hoshizora.model.dto.ImageFileDTO {
        val imageFileDTO = imageService.fetchImageThumbnailFile(imageId)
        return imageFileDTO
    }

    suspend fun handleManagePage(pageRequest: PageRequest): PageResult<ImageManagePageVO> {
        val pageResult = imageService.pageImage(pageRequest)
        return pageResult
    }
}
