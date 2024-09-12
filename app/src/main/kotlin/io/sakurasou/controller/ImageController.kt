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
import io.sakurasou.exception.FileSizeException
import io.sakurasou.extension.*
import java.io.ByteArrayOutputStream

/**
 * @author ShiinaKin
 * 2024/9/5 15:17
 */
fun Route.imageRoute() {
    route("image") {
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
        }, IMAGE_WRITE_SELF)
        {
            val contentLength = call.request.header(HttpHeaders.ContentLength)?.toIntOrNull() ?: 0

            if (contentLength < 0 || TODO("less than Group Limit")) {
                throw FileSizeException()
            }

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
        route("{imageId}", {
            protected = true
            request {
                pathParameter<Long>("imageId") {
                    description = "image id"
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
            }, IMAGE_DELETE_SELF) {
                TODO()
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
            }, IMAGE_WRITE_SELF) {
                TODO()
            }
            get({
                response {
                    HttpStatusCode.OK to {
                        description = "success"
                        body<CommonResponse<ImageVO>> { }
                    }
                }
            }, IMAGE_READ_SELF_SINGLE) {
                TODO()
            }

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
        }, IMAGE_READ_SELF_ALL) {
            TODO()
        }
        route("all", {
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
                delete({
                    response {
                        HttpStatusCode.OK to {
                            description = "success"
                            body<CommonResponse<Unit>> { }
                        }
                    }
                }, IMAGE_DELETE_ALL) {
                    TODO()
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
                }, IMAGE_WRITE_ALL) {
                    TODO()
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
                }, IMAGE_READ_ALL_SINGLE) {
                    TODO()
                }

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
            }, IMAGE_READ_ALL_ALL) {
                TODO()
            }
        }
    }
}

class ImageController {
}