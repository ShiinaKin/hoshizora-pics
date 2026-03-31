@file:OptIn(ExperimentalKtorApi::class)

package io.sakurasou.hoshizora.controller

import io.ktor.http.HttpStatusCode
import io.ktor.openapi.jsonSchema
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi
import io.sakurasou.hoshizora.constant.PERSONAL_ACCESS_TOKEN_READ_SELF
import io.sakurasou.hoshizora.constant.PERSONAL_ACCESS_TOKEN_WRITE_SELF
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.request.PersonalAccessTokenInsertRequest
import io.sakurasou.hoshizora.controller.request.PersonalAccessTokenPatchRequest
import io.sakurasou.hoshizora.controller.vo.CommonResponse
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.PersonalAccessTokenPageVO
import io.sakurasou.hoshizora.exception.controller.param.WrongParameterException
import io.sakurasou.hoshizora.extension.commonResponse
import io.sakurasou.hoshizora.extension.getPrincipal
import io.sakurasou.hoshizora.extension.pageRequest
import io.sakurasou.hoshizora.extension.pageRequestSpec
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.extension.successResponse
import io.sakurasou.hoshizora.extension.transparentRoute
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin
import io.sakurasou.hoshizora.service.personalAccessToken.PersonalAccessTokenService
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * @author ShiinaKin
 * 2024/11/16 05:38
 */

fun Route.personalAccessTokenRoute(personalAccessTokenService: PersonalAccessTokenService) {
    val controller = PersonalAccessTokenController(personalAccessTokenService)
    route("personal-access-token") {
        patInsert(controller)
        route("{patId}") {
            patDelete(controller)
            patPatch(controller)
        }.describe {
            parameters {
                path("patId") {
                    description = "Personal Access Token ID"
                    required = true
                    schema = jsonSchema<Long>()
                }
            }
            responses {
                commonResponse(HttpStatusCode.OK, "Success")
            }
        }
        patPage(controller)
    }.describe {
        tag("PersonalAccessToken")
    }
}

private fun Route.patInsert(controller: PersonalAccessTokenController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = PERSONAL_ACCESS_TOKEN_WRITE_SELF
        }
        install(RequestValidation) {
            validate<PersonalAccessTokenInsertRequest> { insertRequest ->
                if (insertRequest.name.isBlank()) {
                    ValidationResult.Invalid("name is invalid")
                } else if (insertRequest.expireTime < Clock.System.now().toLocalDateTime(TimeZone.UTC)) {
                    ValidationResult.Invalid("expireTime is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        post {
            val userId = call.getPrincipal().id
            val insertRequest = call.receive<PersonalAccessTokenInsertRequest>()
            val token = controller.handleInsert(userId, insertRequest)
            call.success(token)
        }.describe {
            requestBody {
                description = "Personal Access Token Insert Request"
                required = true
                schema = jsonSchema<PersonalAccessTokenInsertRequest>()
            }
            responses {
                successResponse<String>("return personal access token")
            }
        }
    }
}

private fun Route.patDelete(controller: PersonalAccessTokenController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = PERSONAL_ACCESS_TOKEN_WRITE_SELF
        }
        delete {
            val userId = call.getPrincipal().id
            val patId = call.patId()
            controller.handleDelete(userId, patId)
            call.success()
        }
    }
}

private fun Route.patPatch(controller: PersonalAccessTokenController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = PERSONAL_ACCESS_TOKEN_WRITE_SELF
        }
        install(RequestValidation) {
            validate<PersonalAccessTokenPatchRequest> { patchRequest ->
                if (patchRequest.name == null && patchRequest.description == null) {
                    ValidationResult.Invalid("at least one field is required")
                } else if (patchRequest.name != null && patchRequest.name.isBlank()) {
                    ValidationResult.Invalid("name is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch {
            val userId = call.getPrincipal().id
            val patId = call.patId()
            val patchRequest = call.receive<PersonalAccessTokenPatchRequest>()
            controller.handlePatch(userId, patId, patchRequest)
            call.success()
        }.describe {
            requestBody {
                description = "Personal Access Token Patch Request"
                required = true
                schema = jsonSchema<PersonalAccessTokenPatchRequest>()
            }
        }
    }
}

private fun Route.patPage(controller: PersonalAccessTokenController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = PERSONAL_ACCESS_TOKEN_READ_SELF
        }
        get("page") {
            val userId = call.getPrincipal().id
            val pageRequest = call.pageRequest()

            val isExpiredPair =
                call.parameters["isExpired"]?.toBoolean()?.let {
                    "isExpired" to it.toString()
                }

            pageRequest.additionalCondition =
                mutableMapOf<String, String>().apply {
                    isExpiredPair?.let { put(it.first, it.second) }
                }

            val pageResult = controller.handlePage(userId, pageRequest)
            call.success(pageResult)
        }.describe {
            pageRequestSpec()
            parameters {
                query("isExpired") {
                    description = "Whether the token is expired"
                    required = false
                    schema = jsonSchema<Boolean>()
                }
            }
            responses {
                successResponse<PageResult<PersonalAccessTokenPageVO>>("Success")
            }
        }
    }
}

private fun ApplicationCall.patId() = parameters["patId"]?.toLongOrNull() ?: throw WrongParameterException("Invalid patId")

class PersonalAccessTokenController(
    private val personalAccessTokenService: PersonalAccessTokenService,
) {
    suspend fun handleInsert(
        userId: Long,
        insertRequest: PersonalAccessTokenInsertRequest,
    ): String {
        val token = personalAccessTokenService.savePAT(userId, insertRequest)
        return token
    }

    suspend fun handleDelete(
        userId: Long,
        patId: Long,
    ) {
        personalAccessTokenService.deletePAT(userId, patId)
    }

    suspend fun handlePatch(
        userId: Long,
        patId: Long,
        patchRequest: PersonalAccessTokenPatchRequest,
    ) {
        personalAccessTokenService.patchPAT(userId, patId, patchRequest)
    }

    suspend fun handlePage(
        userId: Long,
        pageRequest: PageRequest,
    ): PageResult<PersonalAccessTokenPageVO> {
        val pageResult = personalAccessTokenService.pagePAT(userId, pageRequest)
        return pageResult
    }
}
