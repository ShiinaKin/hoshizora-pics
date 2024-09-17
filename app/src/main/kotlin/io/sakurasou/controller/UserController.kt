package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.sakurasou.constant.*
import io.sakurasou.controller.request.UserManageInsertRequest
import io.sakurasou.controller.request.UserManagePatchRequest
import io.sakurasou.controller.request.UserSelfPatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.UserPageVO
import io.sakurasou.controller.vo.UserVO
import io.sakurasou.extension.getPrincipal
import io.sakurasou.extension.pageRequest
import io.sakurasou.plugins.AuthorizationPlugin
import io.sakurasou.service.user.UserService

/**
 * @author ShiinaKin
 * 2024/9/5 15:35
 */

fun Route.userRoute(userService: UserService) {
    val userController = UserController(userService)
    route("user", {
        protected = true
    }) {
        userSelfRoute(userController)
        userManageRoute(userController)
        banAndUnban(userController)
    }
}

private fun Route.userSelfRoute(userController: UserController) {
    route("self", {
        response {
            HttpStatusCode.NotFound to {
                description = "user not found"
                body<CommonResponse<Unit>> { }
            }
        }
    }) {
        updateSelf(userController)
        fetchSelf(userController)
    }
}

private fun Route.updateSelf(userController: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_WRITE_SELF
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
            TODO()
        }
    }
}

private fun Route.fetchSelf(userController: UserController) {
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
            val principal = call.getPrincipal()
            call.respond(principal)
        }
    }
}

private fun Route.userManageRoute(userController: UserController) {
    route("manage", {
        protected = true
    }) {
        insertUser(userController)
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
            deleteUser(userController)
            updateUser(userController)
            fetchUser(userController)
        }
        pageUser(userController)
    }
}

private fun Route.insertUser(userController: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_WRITE_ALL
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
            TODO()
        }
    }
}

private fun Route.deleteUser(userController: UserController) {
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
            TODO()
        }
    }
}

private fun Route.updateUser(userController: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_WRITE_ALL
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
            TODO()
        }
    }
}

private fun Route.fetchUser(userController: UserController) {
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
            TODO()
        }
    }
}

private fun Route.pageUser(userController: UserController) {
    route {
        install(AuthorizationPlugin) {
            permission = USER_READ_ALL_ALL
        }
        get("page", {
            pageRequest()
            response {
                HttpStatusCode.OK to {
                    description = "success"
                    body<PageResult<UserPageVO>> {
                        description = "page result"
                    }
                }
                HttpStatusCode.BadRequest to {
                    description = "page or pageSize wrong"
                }
            }
        }) {
            val pageVO = call.pageRequest()

            TODO()
        }
    }
}

private fun Route.banAndUnban(userController: UserController) {
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
            TODO()
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
            TODO()
        }
    }
}

class UserController(
    private val userService: UserService
) {

}