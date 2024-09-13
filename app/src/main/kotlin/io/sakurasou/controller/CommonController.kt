package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.routing.*
import io.sakurasou.controller.request.SiteInitRequest
import io.sakurasou.service.common.CommonService

/**
 * @author Shiina Kin
 * 2024/9/9 09:03
 */
fun Route.commonRoute(commonService: CommonService) {
    val commonController = CommonController(commonService)
    route("site") {
        post("init", {
            request {
                body<SiteInitRequest> {
                    required = true
                }
            }
        }) {
            TODO()
        }
    }
    route("fetch") {
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

        }
    }
    route("s") {
        get("{imageId}", {
            request {
                pathParameter<String>("imageId") {
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "strategy local: direct, S3: redirect"
                }
                HttpStatusCode.NotFound to {
                    description = "image not found"
                }
            }
        }) {

        }
    }
}

class CommonController(
    private val commonService: CommonService
) {
}