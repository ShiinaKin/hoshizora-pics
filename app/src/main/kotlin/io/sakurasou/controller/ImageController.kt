package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.sakurasou.constant.*
import io.sakurasou.controller.request.ImagePatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.ImagePageVO
import io.sakurasou.controller.vo.ImageVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.extension.pageRequest
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.image.ImageService
import java.io.ByteArrayOutputStream

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
        imageSelfFetch(controller)
        imageSelfPage(controller)
    }
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
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        })
        {
            val contentLength = call.request.header(HttpHeaders.ContentLength)?.toIntOrNull() ?: 0


            var fileDescription = ""
            var fileName = ""
            val multipartData = call.receiveMultipart()

            val fileBytes = ByteArrayOutputStream(contentLength).apply {
                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            fileDescription = part.value
                        }

                        is PartData.FileItem -> {
                            fileName = part.originalFileName as String
                            writeBytes(part.streamProvider().readBytes())
                        }

                        else -> {}
                    }
                    part.dispose()
                }
            }.toByteArray()

            TODO()

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
            TODO()
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
            TODO()
        }
    }
}

private fun Route.imageSelfFetch(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_SELF_SINGLE
        }
        get({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<ImageVO>> { }
                }
            }
        }) {
            TODO()
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
                    body<PageResult<ImagePageVO>> {
                        description = "page result"
                    }
                }
                HttpStatusCode.BadRequest to {
                    description = "page or pageSize wrong"
                }
            }
        }) {
            TODO()
        }
    }
}

private fun Route.imageManageRoute(controller: ImageController) {
    route("manage", {
        protected = true
        request {
            pathParameter<Long>("id") {
                description = "image id"
                required = true
            }
        }
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
            imageManageFetch(controller)
            imageManagePage(controller)
        }
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
            TODO()
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
            TODO()
        }
    }
}

private fun Route.imageManageFetch(controller: ImageController) {
    route {
        install(AuthorizationPlugin) {
            permission = IMAGE_READ_ALL_SINGLE
        }
        get({
            request {
                pathParameter<Long>("imageId") {
                    description = "image id"
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<ImageVO>> { }
                }
            }
        }) {
            TODO()
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
                    body<PageResult<ImagePageVO>> {
                        description = "page result"
                    }
                }
                HttpStatusCode.BadRequest to {
                    description = "page or pageSize wrong"
                }
            }
        }) {
            TODO()
        }
    }
}

class ImageController(
    private val imageService: ImageService
) {
}