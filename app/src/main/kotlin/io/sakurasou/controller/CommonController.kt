package io.sakurasou.controller

import com.ucasoft.ktor.simpleCache.cacheOutput
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.sakurasou.config.InstanceCenter.commonService
import io.sakurasou.controller.request.SiteInitRequest
import io.sakurasou.exception.controller.param.WrongParameterException
import io.sakurasou.extension.success
import io.sakurasou.model.dto.ImageFileDTO
import io.sakurasou.service.common.CommonService

/**
 * @author Shiina Kin
 * 2024/9/9 09:03
 */
fun Route.commonRoute(commonService: CommonService) {
    val commonController = CommonController(commonService)
    cacheOutput { route("s") { anonymousGetImage(commonController) } }
}

fun Route.siteInitRoute() {
    val commonController = CommonController(commonService)
    post("site/init", {
        request {
            body<SiteInitRequest> {
                required = true
            }
        }
    }) {
        val siteInitRequest = call.receive<SiteInitRequest>()
        commonController.handleInit(siteInitRequest)
        call.success()
    }
}

private fun Route.randomFetchImage(commonController: CommonController) {
    get("random", {
        description = "return random image if setting allow"
        response {
            HttpStatusCode.OK to {
                description = "strategy local: direct, S3: redirect"
            }
            HttpStatusCode.Forbidden to {
                description = "setting not allow"
            }
            HttpStatusCode.NotFound to {
                description = "no image found"
            }
        }
    }) {
        commonController.handleRandomFetchImage()

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
                description = "LOCAL: byte array"
            }
            HttpStatusCode.Found to {
                description = "S3: redirect"
            }
        }
    }) {
        val imageUniqueName = call.parameters["imageUniqueId"]
        if (imageUniqueName == null || imageUniqueName.length != 32) throw WrongParameterException()

        val fileDTO = commonController.handleFetchImage(imageUniqueName)
        if (fileDTO.bytes != null) call.respondBytes(fileDTO.bytes, ContentType.Image.Any)
        else call.respondRedirect(fileDTO.url!!)
    }
}

class CommonController(
    private val commonService: CommonService
) {
    suspend fun handleInit(siteInitRequest: SiteInitRequest) {
        commonService.initSite(siteInitRequest)
    }

    suspend fun handleRandomFetchImage() {

    }

    suspend fun handleFetchImage(imageUniqueName: String): ImageFileDTO {
        val imageFileDTO = commonService.fetchImage(imageUniqueName)
        return imageFileDTO
    }
}