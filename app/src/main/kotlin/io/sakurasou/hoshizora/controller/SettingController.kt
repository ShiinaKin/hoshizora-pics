@file:OptIn(ExperimentalKtorApi::class)

package io.sakurasou.hoshizora.controller

import io.ktor.openapi.jsonSchema
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.patch
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi
import io.sakurasou.hoshizora.constant.REGEX_URL
import io.sakurasou.hoshizora.constant.SETTING_READ
import io.sakurasou.hoshizora.constant.SETTING_SITE
import io.sakurasou.hoshizora.constant.SETTING_SYSTEM
import io.sakurasou.hoshizora.constant.SETTING_WRITE
import io.sakurasou.hoshizora.controller.request.SiteSettingPatchRequest
import io.sakurasou.hoshizora.controller.request.SystemSettingPatchRequest
import io.sakurasou.hoshizora.controller.vo.SettingVO
import io.sakurasou.hoshizora.exception.controller.param.WrongParameterException
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.extension.successResponse
import io.sakurasou.hoshizora.extension.transparentRoute
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin
import io.sakurasou.hoshizora.service.setting.SettingService

/**
 * @author Shiina Kin
 * 2024/9/9 09:00
 */
fun Route.settingRoute(settingService: SettingService) {
    val controller = SettingController(settingService)
    route("setting") {
        fetchSetting(controller)
        handlePatchSiteSetting(controller)
        handlePatchSystemSetting(controller)
    }.describe {
        tag("Setting")
    }
}

private fun Route.fetchSetting(controller: SettingController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = SETTING_READ
        }
        get("{setting_type}") {
            val settingType =
                call.parameters["setting_type"] ?: throw WrongParameterException("setting type is required")
            val settingVO =
                when (settingType) {
                    SETTING_SITE -> controller.handleFetchSiteSetting()
                    SETTING_SYSTEM -> controller.handleFetchSystemSetting()
                    else -> throw WrongParameterException("setting type is invalid")
                }
            call.success(settingVO)
        }.describe {
            description = "get settings"
            parameters {
                path("setting_type") {
                    description = "setting type"
                    schema = jsonSchema<String>()
                }
            }
            responses {
                successResponse<SettingVO>()
            }
        }
    }
}

private fun Route.handlePatchSiteSetting(controller: SettingController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = SETTING_WRITE
        }
        install(RequestValidation) {
            validate<SiteSettingPatchRequest> { patchRequest ->
                if (patchRequest.siteTitle == null &&
                    patchRequest.siteSubtitle == null &&
                    patchRequest.siteDescription == null &&
                    patchRequest.siteExternalUrl == null
                ) {
                    ValidationResult.Invalid("at least one field is required")
                } else if (patchRequest.siteTitle != null && patchRequest.siteTitle.isBlank()) {
                    ValidationResult.Invalid("siteTitle is invalid")
                } else if (patchRequest.siteExternalUrl != null &&
                    !patchRequest.siteExternalUrl.matches(Regex(REGEX_URL))
                ) {
                    ValidationResult.Invalid("siteExternalUrl is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch("site") {
            val siteSettingPatch = call.receive<SiteSettingPatchRequest>()
            controller.handlePatchSiteSetting(siteSettingPatch)
            call.success()
        }.describe {
            description = "site setting"
            requestBody {
                required = true
                schema = jsonSchema<SiteSettingPatchRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.handlePatchSystemSetting(controller: SettingController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = SETTING_WRITE
        }
        install(RequestValidation) {
            validate<SystemSettingPatchRequest> { patchRequest ->
                if (patchRequest.defaultGroupId == null &&
                    patchRequest.allowSignup == null &&
                    patchRequest.allowRandomFetch == null
                ) {
                    ValidationResult.Invalid("at least one field is required")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch("system") {
            val systemSettingPatch = call.receive<SystemSettingPatchRequest>()
            controller.handlePatchSystemSetting(systemSettingPatch)
            call.success()
        }.describe {
            description = "system setting"
            requestBody {
                required = true
                schema = jsonSchema<SystemSettingPatchRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

class SettingController(
    private val settingService: SettingService,
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
