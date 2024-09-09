package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.routing.*
import io.sakurasou.controller.request.SiteSettingPatchRequest
import io.sakurasou.controller.request.StrategySettingPatchRequest
import io.sakurasou.controller.vo.CommonResponse

/**
 * @author Shiina Kin
 * 2024/9/9 09:00
 */
fun Route.settingRoute() {
    route("setting", {
        protected = true
        response {
            HttpStatusCode.OK to {
                description = "success"
                body<CommonResponse<Unit>> { }
            }
        }
    }) {
        patch("site", {
            description = "site setting"
            request {
                body<SiteSettingPatchRequest> {
                    required = true
                }
            }
        }) {
            TODO()
        }
        patch("strategy", {
            description = "strategy setting"
            request {
                body<StrategySettingPatchRequest> {
                    required = true
                }
            }
        }) {
            TODO()
        }
    }
}

class SettingController {
}