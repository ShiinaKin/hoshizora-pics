@file:OptIn(ExperimentalKtorApi::class)

package io.sakurasou.hoshizora.controller

import io.ktor.http.HttpStatusCode
import io.ktor.openapi.jsonSchema
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
import io.sakurasou.hoshizora.constant.REGEX_EMAIL
import io.sakurasou.hoshizora.constant.REGEX_PASSWORD
import io.sakurasou.hoshizora.constant.REGEX_USERNAME
import io.sakurasou.hoshizora.constant.USER_BAN
import io.sakurasou.hoshizora.constant.USER_DELETE
import io.sakurasou.hoshizora.constant.USER_READ_ALL_ALL
import io.sakurasou.hoshizora.constant.USER_READ_ALL_SINGLE
import io.sakurasou.hoshizora.constant.USER_READ_SELF
import io.sakurasou.hoshizora.constant.USER_WRITE_ALL
import io.sakurasou.hoshizora.constant.USER_WRITE_SELF
import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.request.UserManageInsertRequest
import io.sakurasou.hoshizora.controller.request.UserManagePatchRequest
import io.sakurasou.hoshizora.controller.request.UserSelfPatchRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.UserPageVO
import io.sakurasou.hoshizora.controller.vo.UserVO
import io.sakurasou.hoshizora.exception.controller.param.WrongParameterException
import io.sakurasou.hoshizora.extension.commonResponse
import io.sakurasou.hoshizora.extension.getPrincipal
import io.sakurasou.hoshizora.extension.id
import io.sakurasou.hoshizora.extension.pageRequest
import io.sakurasou.hoshizora.extension.pageRequestSpec
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.extension.successResponse
import io.sakurasou.hoshizora.extension.transparentRoute
import io.sakurasou.hoshizora.plugins.AuthorizationPlugin
import io.sakurasou.hoshizora.service.user.UserService

/**
 * @author ShiinaKin
 * 2024/9/5 15:35
 */

fun Route.userRoute(userService: UserService) {
    val controller = UserController(userService)
    route("user") {
        userSelfRoute(controller)
        userManageRoute(controller)
        banAndUnban(controller)
    }.describe {
        tag("User")
    }
}

private fun Route.userSelfRoute(controller: UserController) {
    route("self") {
        patchSelf(controller)
        fetchSelf(controller)
    }.describe {
        responses {
            commonResponse(HttpStatusCode.NotFound, "user not found")
        }
    }
}

private fun Route.patchSelf(controller: UserController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = USER_WRITE_SELF
        }
        install(RequestValidation) {
            validate<UserSelfPatchRequest> { selfPatchRequest ->
                if (selfPatchRequest.password.isNullOrBlank() &&
                    selfPatchRequest.email.isNullOrBlank() &&
                    selfPatchRequest.defaultAlbumId == null &&
                    selfPatchRequest.isDefaultImagePrivate == null
                ) {
                    ValidationResult.Invalid("at least one field is required")
                } else if (selfPatchRequest.password != null && !selfPatchRequest.password.matches(Regex(REGEX_PASSWORD))) {
                    ValidationResult.Invalid("password is invalid")
                } else if (selfPatchRequest.email != null && !selfPatchRequest.email.matches(Regex(REGEX_EMAIL))) {
                    ValidationResult.Invalid("email is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch {
            val id = call.getPrincipal().id
            val patchRequest = call.receive<UserSelfPatchRequest>()
            controller.handleSelfPatch(id, patchRequest)
            call.success()
        }.describe {
            description = "modify self"
            requestBody {
                required = true
                schema = jsonSchema<UserSelfPatchRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.fetchSelf(controller: UserController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = USER_READ_SELF
        }
        get {
            val id = call.getPrincipal().id
            val userVO = controller.handleSelfFetch(id)
            call.success(userVO)
        }.describe {
            responses {
                successResponse<UserVO>()
            }
        }
    }
}

private fun Route.userManageRoute(controller: UserController) {
    route("manage") {
        insertUser(controller)
        route("{id}") {
            deleteUser(controller)
            patchUser(controller)
            fetchUser(controller)
        }.describe {
            parameters {
                path("id") {
                    description = "user id"
                    required = true
                    schema = jsonSchema<Long>()
                }
            }
            responses {
                commonResponse(HttpStatusCode.NotFound, "user not found")
            }
        }
        pageUser(controller)
    }
}

private fun Route.insertUser(controller: UserController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = USER_WRITE_ALL
        }
        install(RequestValidation) {
            validate<UserManageInsertRequest> { manageInsertRequest ->
                if (!manageInsertRequest.username.matches(Regex(REGEX_USERNAME))) {
                    ValidationResult.Invalid("username is invalid")
                } else if (!manageInsertRequest.password.matches(Regex(REGEX_PASSWORD))) {
                    ValidationResult.Invalid("password is invalid")
                } else if (!manageInsertRequest.email.matches(Regex(REGEX_EMAIL))) {
                    ValidationResult.Invalid("email is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        post {
            val insertRequest = call.receive<UserManageInsertRequest>()
            controller.handleManageInsert(insertRequest)
            call.success()
        }.describe {
            description = "admin manual add user"
            requestBody {
                required = true
                schema = jsonSchema<UserManageInsertRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.deleteUser(controller: UserController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = USER_DELETE
        }
        delete {
            val currentUserId = call.getPrincipal().id
            val inputId = call.id()
            if (currentUserId == inputId) throw WrongParameterException()

            controller.handleManageDelete(inputId)
            call.success()
        }.describe {
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.patchUser(controller: UserController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = USER_WRITE_ALL
        }
        install(RequestValidation) {
            validate<UserManagePatchRequest> { managePatchRequest ->
                if (managePatchRequest.password.isNullOrBlank() &&
                    managePatchRequest.email.isNullOrBlank() &&
                    managePatchRequest.defaultAlbumId == null &&
                    managePatchRequest.isDefaultImagePrivate == null &&
                    managePatchRequest.groupId == null
                ) {
                    ValidationResult.Invalid("at least one field is required")
                } else if (managePatchRequest.password != null &&
                    !managePatchRequest.password.matches(
                        Regex(
                            REGEX_PASSWORD,
                        ),
                    )
                ) {
                    ValidationResult.Invalid("password is invalid")
                } else if (managePatchRequest.email != null && !managePatchRequest.email.matches(Regex(REGEX_EMAIL))) {
                    ValidationResult.Invalid("email is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        patch {
            val id = call.id()
            val patchRequest = call.receive<UserManagePatchRequest>()
            controller.handleManagePatch(id, patchRequest)
            call.success()
        }.describe {
            description = "modify any user"
            requestBody {
                required = true
                schema = jsonSchema<UserManagePatchRequest>()
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

private fun Route.fetchUser(controller: UserController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = USER_READ_ALL_SINGLE
        }
        get {
            val id = call.id()
            val userVO = controller.handleManageFetch(id)
            call.success(userVO)
        }.describe {
            responses {
                successResponse<UserVO>()
            }
        }
    }
}

private fun Route.pageUser(controller: UserController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = USER_READ_ALL_ALL
        }
        get("page") {
            val pageRequest = call.pageRequest()
            val isPrivatePair = call.parameters["isBanned"]?.toBoolean()?.let { "isBanned" to it.toString() }
            val usernameSearchPair = call.parameters["username"]?.let { "username" to it }
            pageRequest.additionalCondition =
                mutableMapOf<String, String>().apply {
                    isPrivatePair?.let { put(it.first, it.second) }
                    usernameSearchPair?.let { put(it.first, it.second) }
                }

            val voPageResult = controller.handleManagePage(pageRequest)
            call.success(voPageResult)
        }.describe {
            pageRequestSpec()
            parameters {
                query("isBanned") {
                    description = "is banned"
                    required = false
                    schema = jsonSchema<Boolean>()
                }
                query("username") {
                    description = "search username"
                    required = false
                    schema = jsonSchema<String>()
                }
            }
            responses {
                successResponse<PageResult<UserPageVO>>("page result")
                HttpStatusCode.BadRequest {
                    description = "page or pageSize wrong"
                }
            }
        }
    }
}

private fun Route.banAndUnban(controller: UserController) {
    transparentRoute {
        install(AuthorizationPlugin) {
            permission = USER_BAN
        }
        patch("ban/{id}") {
            val currentUserId = call.getPrincipal().id
            val inputId = call.id()
            if (currentUserId == inputId) throw WrongParameterException()

            controller.handleManageBan(inputId)
            call.success()
        }.describe {
            parameters {
                path("id") {
                    description = "user id"
                    required = true
                    schema = jsonSchema<Long>()
                }
            }
            responses {
                successResponse<Unit>()
            }
        }
        patch("unban/{id}") {
            val currentUserId = call.getPrincipal().id
            val inputId = call.id()
            if (currentUserId == inputId) throw WrongParameterException()

            controller.handleManageUnban(inputId)
            call.success()
        }.describe {
            parameters {
                path("id") {
                    description = "user id"
                    required = true
                    schema = jsonSchema<Long>()
                }
            }
            responses {
                successResponse<Unit>()
            }
        }
    }
}

class UserController(
    private val userService: UserService,
) {
    suspend fun handleSelfPatch(
        id: Long,
        patchRequest: UserSelfPatchRequest,
    ) {
        userService.patchSelf(id, patchRequest)
    }

    suspend fun handleSelfFetch(id: Long): UserVO {
        val userVO = userService.fetchUser(id)
        return userVO
    }

    suspend fun handleManageInsert(insertRequest: UserManageInsertRequest) {
        userService.saveUserManually(insertRequest)
    }

    suspend fun handleManageDelete(id: Long) {
        userService.deleteUser(id)
    }

    suspend fun handleManagePatch(
        id: Long,
        patchRequest: UserManagePatchRequest,
    ) {
        userService.patchUser(id, patchRequest)
    }

    suspend fun handleManageBan(id: Long) {
        userService.banUser(id)
    }

    suspend fun handleManageUnban(id: Long) {
        userService.unbanUser(id)
    }

    suspend fun handleManageFetch(id: Long): UserVO {
        val userVO = userService.fetchUser(id)
        return userVO
    }

    suspend fun handleManagePage(pageRequest: PageRequest): PageResult<UserPageVO> {
        val pageResult = userService.pageUsers(pageRequest)
        return pageResult
    }
}
