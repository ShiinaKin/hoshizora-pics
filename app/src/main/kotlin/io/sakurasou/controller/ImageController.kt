package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
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
import io.sakurasou.exception.controller.param.UnsupportedFileTypeException
import io.sakurasou.exception.controller.param.WrongParameterException
import io.sakurasou.extension.getPrincipal
import io.sakurasou.extension.pageRequest
import io.sakurasou.extension.success
import io.sakurasou.model.dto.ImageFileDTO
import io.sakurasou.plugins.AuthorizationPlugin
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
        imageSelfUpdate(controller)
        imageSelfInfoFetch(controller)
        imageSelfFileFetch(controller)
        imageSelfThumbnailFileFetch(controller)
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

            val imageName = controller.handleSelfUpload(userId, imageRawFile!!)

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

private fun Route.imageSelfUpdate(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_WRITE_SELF
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
        get({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body(Schema<Any>().apply {
                        oneOf(
                            listOf(
                                Schema<String>().apply {
                                    type = "string"
                                    description = "S3"
                                },
                                Schema<ByteArray>().apply {
                                    type = "byteArray"
                                    description = "Local"
                                }
                            )
                        )
                    })
                }
            }
        }) {
            val userId = call.getPrincipal().id
            val imageId = call.imageId()
            val imageFileDTO = controller.handleSelfFileFetch(userId, imageId)
            if (imageFileDTO.bytes != null) call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            else call.respondRedirect(imageFileDTO.url!!)
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
                    body(Schema<Any>().apply {
                        oneOf(
                            listOf(
                                Schema<String>().apply {
                                    type = "string"
                                    description = "S3"
                                },
                                Schema<ByteArray>().apply {
                                    type = "byteArray"
                                    description = "Local"
                                }
                            )
                        )
                    })
                }
            }
        }) {
            val userId = call.getPrincipal().id
            val imageId = call.imageId()
            val imageFileDTO = controller.handleSelfThumbnailFileFetch(userId, imageId)
            if (imageFileDTO.bytes != null) call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            else call.respondRedirect(imageFileDTO.url!!)
        }
    }
}

private fun Route.imageSelfPage(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_SELF_ALL
        }
        get("page", {
            pageRequest()
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
            imageManageUpdate(controller)
            imageManageInfoFetch(controller)
            imageManageFileFetch(controller)
            imageManageThumbnailFileFetch(controller)
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

private fun Route.imageManageUpdate(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_WRITE_ALL
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
        get({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body(Schema<Any>().apply {
                        oneOf(
                            listOf(
                                Schema<String>().apply {
                                    type = "string"
                                    description = "S3"
                                },
                                Schema<ByteArray>().apply {
                                    type = "byteArray"
                                    description = "Local"
                                }
                            )
                        )
                    })
                }
            }
        }) {
            val imageId = call.imageId()
            val imageFileDTO = controller.handleManageFileFetch(imageId)
            if (imageFileDTO.bytes != null) call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            else call.respondRedirect(imageFileDTO.url!!)
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
                    body(Schema<Any>().apply {
                        oneOf(
                            listOf(
                                Schema<String>().apply {
                                    type = "string"
                                    description = "S3"
                                },
                                Schema<ByteArray>().apply {
                                    type = "byteArray"
                                    description = "Local"
                                }
                            )
                        )
                    })
                }
            }
        }) {
            val imageId = call.imageId()
            val imageFileDTO = controller.handleManageThumbnailFileFetch(imageId)
            if (imageFileDTO.bytes != null) call.respondBytes(imageFileDTO.bytes, ContentType.Image.Any)
            else call.respondRedirect(imageFileDTO.url!!)
        }
    }
}

private fun Route.imageManagePage(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_ALL_ALL
        }
        get("page", {
            pageRequest()
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
            val pageRequest = call.pageRequest()
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