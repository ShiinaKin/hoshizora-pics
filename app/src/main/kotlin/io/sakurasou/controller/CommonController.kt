package io.sakurasou.controller

import com.ucasoft.ktor.simpleCache.cacheOutput
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.sakurasou.controller.request.SiteInitRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.CommonSiteSetting
import io.sakurasou.di.InstanceCenter.client
import io.sakurasou.di.InstanceCenter.commonService
import io.sakurasou.exception.controller.param.WrongParameterException
import io.sakurasou.extension.success
import io.sakurasou.model.dto.ImageFileDTO
import io.sakurasou.service.common.CommonService
import kotlin.time.Duration.Companion.hours

/**
 * @author Shiina Kin
 * 2024/9/9 09:03
 */
fun Route.commonRoute(commonService: CommonService) {
    val commonController = CommonController(commonService)
    route({ tags("common") }) {
        cacheOutput(6.hours, listOf("id"), true) { route("random") { randomFetchImage(commonController) } }
        // TODO fix cache
        cacheOutput { route("s") { anonymousGetImage(commonController) } }
    }
}

fun Route.siteInitRoute() {
    val commonController = CommonController(commonService)
    route {
        install(RequestValidation) {
            validate<SiteInitRequest> { siteInitRequest ->
                if (siteInitRequest.siteTitle.isBlank()) ValidationResult.Invalid("siteTitle is invalid")
                else if (!siteInitRequest.siteExternalUrl.matches(Regex("^https?://(.+\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)\$")))
                    ValidationResult.Invalid("siteExternalUrl is invalid")
                else if (!siteInitRequest.username.matches(Regex("^[a-zA-Z0-9]{4,20}\$")))
                    ValidationResult.Invalid("username is invalid")
                else if (!siteInitRequest.password.matches(Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{8,32}\$")))
                    ValidationResult.Invalid("password is invalid")
                else if (!siteInitRequest.email.matches(Regex("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+\$")))
                    ValidationResult.Invalid("email is invalid")
                else ValidationResult.Valid
            }
        }
        post("site/init", {
            request {
                body<SiteInitRequest> {
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val siteInitRequest = call.receive<SiteInitRequest>()
            commonController.handleInit(siteInitRequest)
            call.success()
        }
    }
}

fun Route.commonSiteSettingRoute() {
    val commonController = CommonController(commonService)
    get("site/setting", {
        description = "fetch common site setting"
        response {
            HttpStatusCode.OK to {
                description = "CommonSiteSetting"
                body<CommonResponse<CommonSiteSetting>> { }
            }
        }
    }) {
        val siteSetting: CommonSiteSetting = commonController.fetchCommonSiteSetting()
        call.success(siteSetting)
    }
}

private fun Route.randomFetchImage(commonController: CommonController) {
    get({
        hidden = true
        description = "return random image if setting allow"
        request {
            queryParameter<String>("id") {
                description = "identify resources"
                required = false
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "success"
                body<ByteArray> { }
            }
        }
    }) {
        val fileDTO = commonController.handleRandomFetchImage()
        if (fileDTO.bytes != null) call.respondBytes(fileDTO.bytes, ContentType.Image.Any)
        else call.respondBytes(client.get(fileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
    }
}

private fun Route.anonymousGetImage(commonController: CommonController) {
    get("{imageUniqueId}", {
        hidden = true
        request {
            pathParameter<String>("imageUniqueId") {
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "success"
                body<ByteArray> { }
            }
        }
    }) {
        val imageUniqueName = call.parameters["imageUniqueId"]
        if (imageUniqueName == null || imageUniqueName.length != 32) throw WrongParameterException()

        val fileDTO = commonController.handleFetchImage(imageUniqueName)
        if (fileDTO.bytes != null) call.respondBytes(fileDTO.bytes, ContentType.Image.Any)
        else call.respondBytes(client.get(fileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
    }
}

class CommonController(
    private val commonService: CommonService
) {
    suspend fun handleInit(siteInitRequest: SiteInitRequest) {
        commonService.initSite(siteInitRequest)
    }

    suspend fun fetchCommonSiteSetting(): CommonSiteSetting {
        val siteSetting = commonService.fetchCommonSiteSetting()
        return siteSetting
    }

    suspend fun handleRandomFetchImage(): ImageFileDTO {
        val imageFileDTO = commonService.fetchRandomImage()
        return imageFileDTO
    }

    suspend fun handleFetchImage(imageUniqueName: String): ImageFileDTO {
        val imageFileDTO = commonService.fetchImage(imageUniqueName)
        return imageFileDTO
    }
}