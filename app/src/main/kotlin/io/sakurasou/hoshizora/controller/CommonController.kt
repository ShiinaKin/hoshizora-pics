package io.sakurasou.hoshizora.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.sakurasou.hoshizora.constant.REGEX_EMAIL
import io.sakurasou.hoshizora.constant.REGEX_PASSWORD
import io.sakurasou.hoshizora.constant.REGEX_URL
import io.sakurasou.hoshizora.constant.REGEX_USERNAME
import io.sakurasou.hoshizora.controller.request.SiteInitRequest
import io.sakurasou.hoshizora.controller.vo.CommonResponse
import io.sakurasou.hoshizora.controller.vo.CommonSiteSetting
import io.sakurasou.hoshizora.di.InstanceCenter.client
import io.sakurasou.hoshizora.di.InstanceCenter.commonService
import io.sakurasou.hoshizora.exception.controller.param.WrongParameterException
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.plugins.cache

/**
 * @author Shiina Kin
 * 2024/9/9 09:03
 */
fun Route.commonRoute(commonService: io.sakurasou.hoshizora.service.common.CommonService) {
    val commonController = CommonController(commonService)
    route({ tags("common") }) {
        cache(cachedNoQueryParamRequest = false) {
            rateLimit(RateLimitName("randomFetchLimit")) { route("random") { randomFetchImage(commonController) } }
        }
        cache { rateLimit(RateLimitName("anonymousGetLimit")) { route("s") { anonymousGetImage(commonController) } } }
    }
}

fun Route.siteInitRoute() {
    val commonController = CommonController(commonService)
    route {
        install(RequestValidation) {
            validate<SiteInitRequest> { siteInitRequest ->
                if (siteInitRequest.siteTitle.isBlank()) {
                    ValidationResult.Invalid("siteTitle is invalid")
                } else if (!siteInitRequest.siteExternalUrl.matches(Regex(REGEX_URL))) {
                    ValidationResult.Invalid("siteExternalUrl is invalid")
                } else if (!siteInitRequest.username.matches(Regex(REGEX_USERNAME))) {
                    ValidationResult.Invalid("username is invalid")
                } else if (!siteInitRequest.password.matches(Regex(REGEX_PASSWORD))) {
                    ValidationResult.Invalid("password is invalid")
                } else if (!siteInitRequest.email.matches(Regex(REGEX_EMAIL))) {
                    ValidationResult.Invalid("email is invalid")
                } else {
                    ValidationResult.Valid
                }
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
        if (fileDTO.bytes != null) {
            call.respondBytes(fileDTO.bytes, ContentType.Image.Any)
        } else {
            call.respondBytes(client.get(fileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
        }
    }
}

private fun Route.anonymousGetImage(commonController: CommonController) {
    get("{imageUniqueId}", {
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
        if (fileDTO.bytes != null) {
            call.respondBytes(fileDTO.bytes, ContentType.Image.Any)
        } else {
            call.respondBytes(client.get(fileDTO.url!!).bodyAsBytes(), ContentType.Image.Any)
        }
    }
}

class CommonController(
    private val commonService: io.sakurasou.hoshizora.service.common.CommonService,
) {
    suspend fun handleInit(siteInitRequest: SiteInitRequest) {
        commonService.initSite(siteInitRequest)
    }

    suspend fun fetchCommonSiteSetting(): CommonSiteSetting {
        val siteSetting = commonService.fetchCommonSiteSetting()
        return siteSetting
    }

    suspend fun handleRandomFetchImage(): io.sakurasou.hoshizora.model.dto.ImageFileDTO {
        val imageFileDTO = commonService.fetchRandomImage()
        return imageFileDTO
    }

    suspend fun handleFetchImage(imageUniqueName: String): io.sakurasou.hoshizora.model.dto.ImageFileDTO {
        val imageFileDTO = commonService.fetchImage(imageUniqueName)
        return imageFileDTO
    }
}
