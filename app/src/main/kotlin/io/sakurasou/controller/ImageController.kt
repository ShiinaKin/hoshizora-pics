package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import io.sakurasou.constant.*
import io.sakurasou.controller.request.ImageManagePatchRequest
import io.sakurasou.controller.request.ImagePatchRequest
import io.sakurasou.controller.request.ImageRawFile
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.*
import io.sakurasou.di.InstanceCenter.client
import io.sakurasou.exception.controller.param.UnsupportedFileTypeException
import io.sakurasou.exception.controller.param.WrongParameterException
import io.sakurasou.extension.getPrincipal
import io.sakurasou.extension.pageRequest
import io.sakurasou.extension.pageRequestSpec
import io.sakurasou.extension.success
import io.sakurasou.model.dto.ImageFileDTO
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.plugins.cache
import io.sakurasou.service.image.ImageService
import io.swagger.v3.oas.models.media.Schema
import kotlinx.io.readByteArray

/**
 * @author ShiinaKin
 * 2024/9/5 15:17
 */
fun Route.imageRoute(imageService: ImageService) {
    val controller = ImageController(imageService)
    route("image", {
        protected = true
        tags("Image")
    }) {
        imageSelfRoute(controller)
        imageManageRoute(controller)
    }
}

private fun Route.imageSelfRoute(controller: ImageController) {
    imageSelfUpload(controller)
    route("{imageId}", {
        protected = true
        request {
            pathParameter<Long>("imageId") {
                description = "image id"
                required = true
            }
        }
    }) {
        imageSelfDelete(controller)
        imageSelfPatch(controller)
        imageSelfInfoFetch(controller)
        cache { imageSelfFileFetch(controller) }
        cache { imageSelfThumbnailFileFetch(controller) }
    }
    imageSelfPage(controller)
}

private fun Route.imageSelfUpload(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_WRITE_SELF
        }
        post({
            request {
                multipartBody {
                    required = true
                    mediaTypes = setOf(ContentType.MultiPart.FormData)
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success, return image external id, or empty string if it is private"
                    body<CommonResponse<String>> { }
                }
            }
        })
        {
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

                        imageRawFile = ImageRawFile(
                            name = fileName,
                            mimeType = mimeType,
                            size = contentLength,
                            bytes = bytes
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
        }
    }
}

private fun Route.imageSelfDelete(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_DELETE_SELF
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
            val imageId = call.imageId()
            controller.handleSelfDelete(userId, imageId)
            call.success()
        }
    }
}

private fun Route.imageSelfPatch(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_WRITE_SELF
        }
        install(RequestValidation) {
            validate<ImagePatchRequest> { patchRequest ->
                if (patchRequest.albumId == null
                    && patchRequest.displayName == null
                    && patchRequest.description == null
                    && patchRequest.isPrivate == null
                    && patchRequest.isAllowedRandomFetch == null
                ) ValidationResult.Invalid("at least one field should be provided")
                else if (patchRequest.displayName != null && patchRequest.displayName.isBlank())
                    ValidationResult.Invalid("displayName is invalid")
                else ValidationResult.Valid
            }
        }
        patch({
            request {
                body<ImagePatchRequest> {
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
            val imageId = call.imageId()
            val imagePatchRequest = call.receive<ImagePatchRequest>()
            controller.handleSelfUpdate(userId, imageId, imagePatchRequest)
            call.success()
        }
    }
}

private fun Route.imageSelfInfoFetch(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_SELF_SINGLE
        }
        get("info", {
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<ImageVO>> { }
                }
            }
        }) {
            val userId = call.getPrincipal().id
            val imageId = call.imageId()
            val imageVO = controller.handleSelfFetch(userId, imageId)
            call.success(imageVO)
        }
    }
}

private fun Route.imageSelfFileFetch(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_SELF_SINGLE
        }
        get("file", {
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    Schema<Any>().apply {
                        type = "blob"
                    }
                }
            }
        }) {
            val userId = call.getPrincipal().id
            val imageId = call.imageId()
            val imageFileDTO = controller.handleSelfFileFetch(userId, imageId)
            if (imageFileDTO.bytes != null) call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            else call.respondBytes(client.get(imageFileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
        }
    }
}

private fun Route.imageSelfThumbnailFileFetch(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_SELF_SINGLE
        }
        get("thumbnail", {
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    Schema<Any>().apply {
                        type = "blob"
                    }
                }
            }
        }) {
            val userId = call.getPrincipal().id
            val imageId = call.imageId()
            val imageFileDTO = controller.handleSelfThumbnailFileFetch(userId, imageId)
            if (imageFileDTO.bytes != null) call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            else call.respondBytes(client.get(imageFileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
        }
    }
}

private fun Route.imageSelfPage(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_SELF_ALL
        }
        get("page", {
            pageRequestSpec()
            request {
                queryParameter<Long>("albumId") {
                    description = "albumId"
                    required = false
                }
                queryParameter<Boolean>("isPrivate") {
                    description = "isPrivate"
                    required = false
                }
                queryParameter<String>("search") {
                    description = "search content"
                    required = false
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<PageResult<ImagePageVO>>> {
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
            val albumIdPair = call.parameters["albumId"]?.toLongOrNull()?.let { "albumId" to it.toString() }
            val isPrivatePair = call.parameters["isPrivate"]?.toBoolean()?.let { "isPrivate" to it.toString() }
            val searchPair = call.parameters["search"]?.let { "search" to it }
            pageRequest.additionalCondition = mutableMapOf<String, String>().apply {
                albumIdPair?.let { put(it.first, it.second) }
                isPrivatePair?.let { put(it.first, it.second) }
                searchPair?.let { put(it.first, it.second) }
            }

            val pageResult = controller.handleSelfPage(userId, pageRequest)
            call.success(pageResult)
        }
    }
}

private fun Route.imageManageRoute(controller: ImageController) {
    route("manage", {
        protected = true
        response {
            HttpStatusCode.NotFound to {
                description = "image not found"
                body<CommonResponse<Unit>> { }
            }
        }
    }) {
        route("{imageId}", {
            request {
                pathParameter<Long>("imageId") {
                    description = "image id"
                    required = true
                }
            }
        }) {
            imageManageDelete(controller)
            imageManagePatch(controller)
            imageManageInfoFetch(controller)
            cache { imageManageFileFetch(controller) }
            cache { imageManageThumbnailFileFetch(controller) }
        }
        imageManagePage(controller)
    }
}

private fun Route.imageManageDelete(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_DELETE_ALL
        }
        delete({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val imageId = call.imageId()
            controller.handleManageDelete(imageId)
            call.success()
        }
    }
}

private fun Route.imageManagePatch(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_WRITE_ALL
        }
        install(RequestValidation) {
            validate<ImageManagePatchRequest> { managePatchRequest ->
                if (managePatchRequest.userId == null
                    && managePatchRequest.albumId == null
                    && managePatchRequest.displayName == null
                    && managePatchRequest.description == null
                    && managePatchRequest.isPrivate == null
                    && managePatchRequest.isAllowedRandomFetch == null
                ) ValidationResult.Invalid("at least one field should be provided")
                else if (managePatchRequest.displayName != null && managePatchRequest.displayName.isBlank())
                    ValidationResult.Invalid("displayName is invalid")
                else ValidationResult.Valid
            }
        }
        patch({
            request {
                body<ImageManagePatchRequest> {
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
            val imageId = call.imageId()
            val imagePatchRequest = call.receive<ImageManagePatchRequest>()
            controller.handleManageUpdate(imageId, imagePatchRequest)
            call.success()
        }
    }
}

private fun Route.imageManageInfoFetch(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_ALL_SINGLE
        }
        get("info", {
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<ImageManageVO>> { }
                }
            }
        }) {
            val imageId = call.imageId()
            val imageManageVO = controller.handleManageFetch(imageId)
            call.success(imageManageVO)
        }
    }
}

private fun Route.imageManageFileFetch(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_ALL_SINGLE
        }
        get("file", {
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    Schema<Any>().apply {
                        type = "blob"
                    }
                }
            }
        }) {
            val imageId = call.imageId()
            val imageFileDTO = controller.handleManageFileFetch(imageId)
            if (imageFileDTO.bytes != null) call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            else call.respondBytes(client.get(imageFileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
        }
    }
}

private fun Route.imageManageThumbnailFileFetch(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_ALL_SINGLE
        }
        get("thumbnail", {
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    Schema<Any>().apply {
                        type = "blob"
                    }
                }
            }
        }) {
            val imageId = call.imageId()
            val imageFileDTO = controller.handleManageThumbnailFileFetch(imageId)
            if (imageFileDTO.bytes != null) call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            else call.respondBytes(client.get(imageFileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
        }
    }
}

private fun Route.imageManagePage(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_ALL_ALL
        }
        get("page", {
            pageRequestSpec()
            request {
                queryParameter<Long>("userId") {
                    description = "userId"
                    required = false
                }
                queryParameter<Long>("albumId") {
                    description = "albumId"
                    required = false
                }
                queryParameter<Boolean>("isPrivate") {
                    description = "isPrivate"
                    required = false
                }
                queryParameter<String>("search") {
                    description = "search content"
                    required = false
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<PageResult<ImageManagePageVO>>> {
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
            val albumIdPair = call.parameters["albumId"]?.toLongOrNull()?.let { "albumId" to it.toString() }
            val isPrivatePair = call.parameters["isPrivate"]?.toBoolean()?.let { "isPrivate" to it.toString() }
            val searchPair = call.parameters["search"]?.let { "search" to it }
            pageRequest.additionalCondition = mutableMapOf<String, String>().apply {
                userIdPair?.let { put(it.first, it.second) }
                albumIdPair?.let { put(it.first, it.second) }
                isPrivatePair?.let { put(it.first, it.second) }
                searchPair?.let { put(it.first, it.second) }
            }

            val pageResult = controller.handleManagePage(pageRequest)
            call.success(pageResult)
        }
    }
}

private fun ApplicationCall.imageId() =
    parameters["imageId"]?.toLongOrNull() ?: throw IllegalArgumentException("imageId")

class ImageController(
    private val imageService: ImageService
) {
    suspend fun handleSelfUpload(userId: Long, imageRawFile: ImageRawFile): String {
        val imageName = imageService.saveImage(userId, imageRawFile)
        return imageName
    }

    suspend fun handleSelfDelete(userId: Long, imageId: Long) {
        imageService.deleteSelfImage(userId, imageId)
    }

    suspend fun handleSelfUpdate(userId: Long, imageId: Long, imagePatchRequest: ImagePatchRequest) {
        imageService.patchSelfImage(userId, imageId, imagePatchRequest)
    }

    suspend fun handleSelfFetch(userId: Long, imageId: Long): ImageVO {
        val imageVO = imageService.fetchSelfImageInfo(userId, imageId)
        return imageVO
    }

    suspend fun handleSelfFileFetch(userId: Long, imageId: Long): ImageFileDTO {
        val imageFileDTO = imageService.fetchSelfImageFile(userId, imageId)
        return imageFileDTO
    }

    suspend fun handleSelfThumbnailFileFetch(userId: Long, imageId: Long): ImageFileDTO {
        val imageFileDTO = imageService.fetchSelfImageThumbnailFile(userId, imageId)
        return imageFileDTO
    }

    suspend fun handleSelfPage(userId: Long, pageRequest: PageRequest): PageResult<ImagePageVO> {
        val pageResult = imageService.pageSelfImage(userId, pageRequest)
        return pageResult
    }

    suspend fun handleManageDelete(imageId: Long) {
        imageService.deleteImage(imageId)
    }

    suspend fun handleManageUpdate(imageId: Long, imagePatchRequest: ImageManagePatchRequest) {
        imageService.patchImage(imageId, imagePatchRequest)
    }

    suspend fun handleManageFetch(imageId: Long): ImageManageVO {
        val imageManageVO = imageService.fetchImageInfo(imageId)
        return imageManageVO
    }

    suspend fun handleManageFileFetch(imageId: Long): ImageFileDTO {
        val imageFileDTO = imageService.fetchImageFile(imageId)
        return imageFileDTO
    }

    suspend fun handleManageThumbnailFileFetch(imageId: Long): ImageFileDTO {
        val imageFileDTO = imageService.fetchImageThumbnailFile(imageId)
        return imageFileDTO
    }

    suspend fun handleManagePage(pageRequest: PageRequest): PageResult<ImageManagePageVO> {
        val pageResult = imageService.pageImage(pageRequest)
        return pageResult
    }
}