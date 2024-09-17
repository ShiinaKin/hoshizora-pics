package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.sakurasou.constant.SETTING_READ
import io.sakurasou.constant.SETTING_WRITE
import io.sakurasou.controller.request.SiteSettingPatchRequest
import io.sakurasou.controller.request.StrategySettingPatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.SettingVO
import io.sakurasou.plugins.AuthorizationPlugin

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
        fetchAllSetting()
        updateSiteSetting()
        updateStrategySetting()
        updateSystemSetting()
    }
}

private fun Route.fetchAllSetting() {
    route {
        install(AuthorizationPlugin) {
            permission = SETTING_READ
        }
        get({
            description = "get all settings"
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<List<SettingVO>>> { }
                }
            }
        }) {
            TODO()
        }
    }
}

private fun Route.updateSiteSetting() {
    route {
        install(AuthorizationPlugin) {
            permission = SETTING_WRITE
        }
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
    }
}

private fun Route.updateStrategySetting() {
    route {
        install(AuthorizationPlugin) {
            permission = SETTING_WRITE
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

private fun Route.updateSystemSetting() {
    route {
        install(AuthorizationPlugin) {
            permission = SETTING_WRITE
        }
        patch("system", {
            description = "system setting"
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