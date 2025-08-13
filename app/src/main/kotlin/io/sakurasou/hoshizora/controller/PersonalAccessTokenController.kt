package io.sakurasou.hoshizora.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.patch
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.sakurasou.hoshizora.constant.PERSONAL_ACCESS_TOKEN_READ_SELF
import io.sakurasou.hoshizora.constant.PERSONAL_ACCESS_TOKEN_WRITE_SELF
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.request.PersonalAccessTokenInsertRequest
import io.sakurasou.hoshizora.controller.request.PersonalAccessTokenPatchRequest
import io.sakurasou.hoshizora.controller.vo.CommonResponse
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.PersonalAccessTokenPageVO
import io.sakurasou.hoshizora.exception.controller.param.WrongParameterException
import io.sakurasou.hoshizora.extension.getPrincipal
import io.sakurasou.hoshizora.extension.pageRequest
import io.sakurasou.hoshizora.extension.pageRequestSpec
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author ShiinaKin
 * 2024/11/16 05:38
 */

fun Route.personalAccessTokenRoute(
    personalAccessTokenService: io.sakurasou.hoshizora.service.personalAccessToken.PersonalAccessTokenService,
) {
    val controller = PersonalAccessTokenController(personalAccessTokenService)
    route("personal-access-token", {
        protected = true
        tags("PersonalAccessToken")
    }) {
        patInsert(controller)
        route("{patId}", {
            request {
                pathParameter<Long>("patId") {
                    description = "Personal Access Token ID"
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    body<CommonResponse<Unit>> {
                        description = "Success"
                    }
                }
            }
        }) {
            patDelete(controller)
            patPatch(controller)
        }
        patPage(controller)
    }
}

private fun Route.patInsert(controller: PersonalAccessTokenController) {
    route {
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
        post({
            request {
                body<PersonalAccessTokenInsertRequest> {
                    description = "Personal Access Token Insert Request"
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    body<CommonResponse<String>> {
                        description = "return personal access token"
                    }
                }
            }
        }) {
            val userId = call.getPrincipal().id
            val insertRequest = call.receive<PersonalAccessTokenInsertRequest>()
            val token = controller.handleInsert(userId, insertRequest)
            call.success(token)
        }
    }
}

private fun Route.patDelete(controller: PersonalAccessTokenController) {
    route {
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
    route {
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
        patch({
            request {
                body<PersonalAccessTokenPatchRequest> {
                    description = "Personal Access Token Patch Request"
                    required = true
                }
            }
        }) {
            val userId = call.getPrincipal().id
            val patId = call.patId()
            val patchRequest = call.receive<PersonalAccessTokenPatchRequest>()
            controller.handlePatch(userId, patId, patchRequest)
            call.success()
        }
    }
}

private fun Route.patPage(controller: PersonalAccessTokenController) {
    route {
        install(AuthorizationPlugin) {
            permission = PERSONAL_ACCESS_TOKEN_READ_SELF
        }
        get("page", {
            pageRequestSpec()
            request {
                queryParameter<Boolean>("isExpired") {
                    description = "Whether the token is expired"
                    required = false
                }
            }
            response {
                HttpStatusCode.OK to {
                    body<CommonResponse<PageResult<PersonalAccessTokenPageVO>>> {
                        description = "Success"
                    }
                }
            }
        }) {
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
        }
    }
}

private fun ApplicationCall.patId() = parameters["patId"]?.toLongOrNull() ?: throw WrongParameterException("Invalid patId")

class PersonalAccessTokenController(
    private val personalAccessTokenService: io.sakurasou.hoshizora.service.personalAccessToken.PersonalAccessTokenService,
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
