package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.*
import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.sakurasou.constant.*
import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.request.UserManageInsertRequest
import io.sakurasou.controller.request.UserManagePatchRequest
import io.sakurasou.controller.request.UserSelfPatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.UserPageVO
import io.sakurasou.controller.vo.UserVO
import io.sakurasou.exception.controller.param.WrongParameterException
import io.sakurasou.extension.*
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.user.UserService

/**
 * @author ShiinaKin
 * 2024/9/5 15:35
 */

fun Route.userRoute(userService: UserService) {
    val controller = UserController(userService)
    route("user", {
        protected = true
        tags("User")
    }) {
        userSelfRoute(controller)
        userManageRoute(controller)
        banAndUnban(controller)
    }
}

private fun Route.userSelfRoute(controller: UserController) {
    route("self", {
        response {
            HttpStatusCode.NotFound to {
                description = "user not found"
                body<CommonResponse<Unit>> { }
            }
        }
    }) {
        patchSelf(controller)
        fetchSelf(controller)
    }
}

private fun Route.patchSelf(controller: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_WRITE_SELF
        }
        install(RequestValidation) {
            validate<UserSelfPatchRequest> { selfPatchRequest ->
                if (selfPatchRequest.password.isNullOrBlank()
                    && selfPatchRequest.email.isNullOrBlank()
                    && selfPatchRequest.defaultAlbumId == null
                    && selfPatchRequest.isDefaultImagePrivate == null
                )
                    ValidationResult.Invalid("at least one field is required")
                else if (selfPatchRequest.password != null && !selfPatchRequest.password.matches(Regex(REGEX_PASSWORD)))
                    ValidationResult.Invalid("password is invalid")
                else if (selfPatchRequest.email != null && !selfPatchRequest.email.matches(Regex(REGEX_EMAIL)))
                    ValidationResult.Invalid("email is invalid")
                else ValidationResult.Valid
            }
        }
        patch({
            description = "modify self"
            request {
                body<UserSelfPatchRequest> {
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val id = call.getPrincipal().id
            val patchRequest = call.receive<UserSelfPatchRequest>()
            controller.handleSelfPatch(id, patchRequest)
            call.success()
        }
    }
}

private fun Route.fetchSelf(controller: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_READ_SELF
        }
        get({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<UserVO>> { }
                }
            }
        }) {
            val id = call.getPrincipal().id
            val userVO = controller.handleSelfFetch(id)
            call.success(userVO)
        }
    }
}

private fun Route.userManageRoute(controller: UserController) {
    route("manage", {
        protected = true
    }) {
        insertUser(controller)
        route("{id}", {
            request {
                pathParameter<Long>("id") {
                    description = "user id"
                    required = true
                }
            }
            response {
                HttpStatusCode.NotFound to {
                    description = "user not found"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            deleteUser(controller)
            patchUser(controller)
            fetchUser(controller)
        }
        pageUser(controller)
    }
}

private fun Route.insertUser(controller: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_WRITE_ALL
        }
        install(RequestValidation) {
            validate<UserManageInsertRequest> { manageInsertRequest ->
                if (!manageInsertRequest.username.matches(Regex(REGEX_USERNAME)))
                    ValidationResult.Invalid("username is invalid")
                else if (!manageInsertRequest.password.matches(Regex(REGEX_PASSWORD)))
                    ValidationResult.Invalid("password is invalid")
                else if (!manageInsertRequest.email.matches(Regex(REGEX_EMAIL)))
                    ValidationResult.Invalid("email is invalid")
                else ValidationResult.Valid
            }
        }
        post({
            description = "admin manual add user"
            request {
                body<UserManageInsertRequest> {
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val insertRequest = call.receive<UserManageInsertRequest>()
            controller.handleManageInsert(insertRequest)
            call.success()
        }
    }
}

private fun Route.deleteUser(controller: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_DELETE
        }
        delete({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val currentUserId = call.getPrincipal().id
            val inputId = call.id()
            if (currentUserId == inputId) throw WrongParameterException()

            controller.handleManageDelete(inputId)
            call.success()
        }
    }
}

private fun Route.patchUser(controller: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_WRITE_ALL
        }
        install(RequestValidation) {
            validate<UserManagePatchRequest> { managePatchRequest ->
                if (managePatchRequest.password.isNullOrBlank()
                    && managePatchRequest.email.isNullOrBlank()
                    && managePatchRequest.defaultAlbumId == null
                    && managePatchRequest.isDefaultImagePrivate == null
                    && managePatchRequest.groupId == null
                )
                    ValidationResult.Invalid("at least one field is required")
                else if (managePatchRequest.password != null && !managePatchRequest.password.matches(Regex(REGEX_PASSWORD)))
                    ValidationResult.Invalid("password is invalid")
                else if (managePatchRequest.email != null && !managePatchRequest.email.matches(Regex(REGEX_EMAIL)))
                    ValidationResult.Invalid("email is invalid")
                else ValidationResult.Valid
            }
        }
        patch({
            description = "modify any user"
            request {
                body<UserManagePatchRequest> {
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val id = call.id()
            val patchRequest = call.receive<UserManagePatchRequest>()
            controller.handleManagePatch(id, patchRequest)
            call.success()
        }
    }
}

private fun Route.fetchUser(controller: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_READ_ALL_SINGLE
        }
        get({
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<UserVO>> { }
                }
            }
        }) {
            val id = call.id()
            val userVO = controller.handleManageFetch(id)
            call.success(userVO)
        }
    }
}

private fun Route.pageUser(controller: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_READ_ALL_ALL
        }
        get("page", {
            pageRequestSpec()
            request {
                queryParameter<Boolean>("isBanned") {
                    description = "is banned"
                    required = false
                }
                queryParameter<String>("username") {
                    description = "search username"
                    required = false
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<PageResult<UserPageVO>>> {
                        description = "page result"
                    }
                }
                HttpStatusCode.BadRequest to {
                    description = "page or pageSize wrong"
                }
            }
        }) {
            val pageRequest = call.pageRequest()
            val isPrivatePair = call.parameters["isBanned"]?.toBoolean()?.let { "isBanned" to it.toString() }
            val usernameSearchPair = call.parameters["username"]?.let { "username" to it }
            pageRequest.additionalCondition = mutableMapOf<String, String>().apply {
                isPrivatePair?.let { put(it.first, it.second) }
                usernameSearchPair?.let { put(it.first, it.second) }
            }

            val voPageResult = controller.handleManagePage(pageRequest)
            call.success(voPageResult)
        }
    }
}

private fun Route.banAndUnban(controller: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_BAN
        }
        patch("ban/{id}", {
            request {
                pathParameter<Long>("id") {
                    description = "user id"
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val currentUserId = call.getPrincipal().id
            val inputId = call.id()
            if (currentUserId == inputId) throw WrongParameterException()

            controller.handleManageBan(inputId)
            call.success()
        }
        patch("unban/{id}", {
            request {
                pathParameter<Long>("id") {
                    description = "user id"
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<CommonResponse<Unit>> { }
                }
            }
        }) {
            val currentUserId = call.getPrincipal().id
            val inputId = call.id()
            if (currentUserId == inputId) throw WrongParameterException()

            controller.handleManageUnban(inputId)
            call.success()
        }
    }
}

class UserController(
    private val userService: UserService
) {
    suspend fun handleSelfPatch(id: Long, patchRequest: UserSelfPatchRequest) {
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

    suspend fun handleManagePatch(id: Long, patchRequest: UserManagePatchRequest) {
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