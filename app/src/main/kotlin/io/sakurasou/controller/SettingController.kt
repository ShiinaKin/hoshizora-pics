package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.sakurasou.constant.REGEX_URL
import io.sakurasou.constant.SETTING_READ
import io.sakurasou.constant.SETTING_SITE
import io.sakurasou.constant.SETTING_SYSTEM
import io.sakurasou.constant.SETTING_WRITE
import io.sakurasou.controller.request.SiteSettingPatchRequest
import io.sakurasou.controller.request.SystemSettingPatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.SettingVO
import io.sakurasou.exception.controller.param.WrongParameterException
import io.sakurasou.extension.success
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.setting.SettingService

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
        fetchSetting(controller)
        handlePatchSiteSetting(controller)
        handlePatchSystemSetting(controller)
    }
}

private fun Route.fetchSetting(controller: SettingController) {
    route {
        install(AuthorizationPlugin) {
            permission = SETTING_READ
        }
        get("{setting_type}", {
            description = "get settings"
            request {
                pathParameter<String>("setting_type") {
                    description = "setting type"
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<SettingVO>>()
                }
            }
        }) {
            val settingType =
                call.parameters["setting_type"] ?: throw WrongParameterException("setting type is required")
            val settingVO = when (settingType) {
                SETTING_SITE -> controller.handleFetchSiteSetting()
                SETTING_SYSTEM -> controller.handleFetchSystemSetting()
                else -> throw WrongParameterException("setting type is invalid")
            }
            call.success(settingVO)
        }
    }
}

private fun Route.handlePatchSiteSetting(controller: SettingController) {
    route {
        install(AuthorizationPlugin) {
            permission = SETTING_WRITE
        }
        install(RequestValidation) {
            validate<SiteSettingPatchRequest> { patchRequest ->
                if (patchRequest.siteTitle == null
                    && patchRequest.siteSubtitle == null
                    && patchRequest.siteDescription == null
                    && patchRequest.siteExternalUrl == null
                ) ValidationResult.Invalid("at least one field is required")
                else if (patchRequest.siteTitle != null && patchRequest.siteTitle.isBlank())
                    ValidationResult.Invalid("siteTitle is invalid")
                else if (patchRequest.siteExternalUrl != null
                    && !patchRequest.siteExternalUrl.matches(Regex(REGEX_URL)))
                    ValidationResult.Invalid("siteExternalUrl is invalid")
                else ValidationResult.Valid
            }
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
        install(RequestValidation) {
            validate<SystemSettingPatchRequest> { patchRequest ->
                if (patchRequest.defaultGroupId == null
                    && patchRequest.allowSignup == null
                    && patchRequest.allowRandomFetch == null
                ) ValidationResult.Invalid("at least one field is required")
                else ValidationResult.Valid
            }
        }
        patch("system", {
            description = "system setting"
            request {
                body<SystemSettingPatchRequest> {
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
    suspend fun handleFetchSiteSetting(): SettingVO {
        val siteSetting = settingService.getSiteSetting()
        return SettingVO(
            SETTING_SITE,
            siteSetting,
        )
    }

    suspend fun handleFetchSystemSetting(): SettingVO {
        val systemSetting = settingService.getSystemSetting()
        return SettingVO(
            SETTING_SYSTEM,
            systemSetting,
        )
    }

    suspend fun handlePatchSiteSetting(siteSettingPatch: SiteSettingPatchRequest) {
        settingService.updateSiteSetting(siteSettingPatch)
    }

    suspend fun handlePatchSystemSetting(systemSettingPatch: SystemSettingPatchRequest) {
        settingService.updateSystemSetting(systemSettingPatch)
    }
}