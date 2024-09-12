package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.routing.*
import io.sakurasou.constant.SETTING_READ
import io.sakurasou.constant.SETTING_WRITE
import io.sakurasou.controller.request.SiteSettingPatchRequest
import io.sakurasou.controller.request.StrategySettingPatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.SettingVO
import io.sakurasou.extension.get
import io.sakurasou.extension.patch

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
        get({
            description = "get all settings"
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<List<SettingVO>>> { }
                }
            }
        }, SETTING_READ) {
            TODO()
        }
        patch("site", {
            description = "site setting"
            request {
                body<SiteSettingPatchRequest> {
                    required = true
                }
            }
        }, SETTING_WRITE) {
            TODO()
        }
        patch("strategy", {
            description = "strategy setting"
            request {
                body<StrategySettingPatchRequest> {
                    required = true
                }
            }
        }, SETTING_WRITE) {
            TODO()
        }
    }
}

class SettingController {
}