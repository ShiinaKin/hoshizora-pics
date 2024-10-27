package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.data.ref
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.sakurasou.constant.SETTING_READ
import io.sakurasou.constant.SETTING_SITE
import io.sakurasou.constant.SETTING_SYSTEM
import io.sakurasou.constant.SETTING_WRITE
import io.sakurasou.controller.request.SiteSettingPatchRequest
import io.sakurasou.controller.request.StrategySettingPatchRequest
import io.sakurasou.controller.request.SystemSettingPatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.SettingVO
import io.sakurasou.extension.success
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.setting.SettingService
import io.swagger.v3.oas.models.media.Schema

/**
 * @author Shiina Kin
 * 2024/9/9 09:00
 */
fun Route.settingRoute(settingService: SettingService) {
    val controller = SettingController(settingService)
    route("setting", {
        protected = true
        tags("Setting")
        response {
            HttpStatusCode.OK to {
                description = "success"
                body<CommonResponse<Unit>> { }
            }
        }
    }) {
        fetchAllSetting(controller)
        handlePatchSiteSetting(controller)
        handlePatchSystemSetting(controller)
    }
}

private fun Route.fetchAllSetting(controller: SettingController) {
    route {
        install(AuthorizationPlugin) {
            permission = SETTING_READ
        }
        get({
            description = "get all settings"
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body(Schema<CommonResponse<Map<String, SettingVO>>>().apply {
                        title = "CommonResponseSettingVOMap"
                        type = "object"
                        addProperty("code", Schema<Int>().apply { type = "integer" })
                        addProperty("message", Schema<String>().apply { type = "string" })
                        addProperty("data", Schema<Map<String, SettingVO>>().apply {
                            type = "object"
                            additionalProperties = Schema<SettingVO>().apply {
                                type = "object"
                                ref("#/components/schemas/SettingVO")
                            }
                        })
                        addProperty("isSuccessful", Schema<Boolean>().apply { type = "boolean" })
                    })
                }
            }
        }) {
            val settingVOMap = controller.handleFetchAllSetting()
            call.success(settingVOMap)
        }
    }
}

private fun Route.handlePatchSiteSetting(controller: SettingController) {
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
            val siteSettingPatch = call.receive<SiteSettingPatchRequest>()
            controller.handlePatchSiteSetting(siteSettingPatch)
            call.success()
        }
    }
}

private fun Route.handlePatchSystemSetting(controller: SettingController) {
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
            val systemSettingPatch = call.receive<SystemSettingPatchRequest>()
            controller.handlePatchSystemSetting(systemSettingPatch)
            call.success()
        }
    }
}

class SettingController(
    private val settingService: SettingService
) {
    suspend fun handleFetchAllSetting(): Map<String, SettingVO> {
        val systemSetting = settingService.getSystemSetting()
        val siteSetting = settingService.getSiteSetting()
        val systemSettingVO = SettingVO(
            SETTING_SYSTEM,
            systemSetting,
        )
        val siteSettingVO = SettingVO(
            SETTING_SITE,
            siteSetting,
        )
        return mapOf(
            SETTING_SYSTEM to systemSettingVO,
            SETTING_SITE to siteSettingVO,
        )
    }

    suspend fun handlePatchSiteSetting(siteSettingPatch: SiteSettingPatchRequest) {
        settingService.updateSiteSetting(siteSettingPatch)
    }

    suspend fun handlePatchSystemSetting(systemSettingPatch: SystemSettingPatchRequest) {
        settingService.updateSystemSetting(systemSettingPatch)
    }
}