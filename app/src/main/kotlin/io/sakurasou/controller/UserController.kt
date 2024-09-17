package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.sakurasou.constant.*
import io.sakurasou.controller.request.UserPatchRequest
import io.sakurasou.controller.request.UserSelfPatchRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.UserPageVO
import io.sakurasou.controller.vo.UserVO
import io.sakurasou.extension.delete
import io.sakurasou.extension.get
import io.sakurasou.extension.pageRequest
import io.sakurasou.extension.patch
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
    }, USER_WRITE_SELF) {
        TODO()
    }

}

private fun Route.fetchSelf(userController: UserController) {
    get({
        response {
            HttpStatusCode.OK to {
                description = "success"
                body<CommonResponse<UserVO>> { }
            }
        }
    }, USER_READ_SELF) {
        TODO()
    }
}

private fun Route.userManageRoute(userController: UserController) {
    route("all", {
        protected = true
    }) {
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

private fun Route.deleteUser(userController: UserController) {
    delete({
        response {
            HttpStatusCode.OK to {
                description = "success"
                body<CommonResponse<Unit>> { }
            }
        }
    }, USER_DELETE) {
        TODO()
    }
}

private fun Route.updateUser(userController: UserController) {
    patch({
        description = "modify any user"
        request {
            body<UserPatchRequest> {
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                description = "success"
                body<CommonResponse<Unit>> { }
            }
        }
    }, USER_WRITE_ALL) {
        TODO()
    }
}

private fun Route.fetchUser(userController: UserController) {
    get({
        response {
            HttpStatusCode.OK to {
                description = "success"
                body<CommonResponse<UserVO>> { }
            }
        }
    }, USER_READ_ALL_SINGLE) {
        TODO()
    }
}

private fun Route.pageUser(userController: UserController) {
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
    }, USER_READ_ALL_ALL) {
        val pageVO = call.pageRequest()

        TODO()
    }
}

private fun Route.banAndUnban(userController: UserController) {
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
    }, USER_BAN) {
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
    }, USER_BAN) {
        TODO()
    }
}

class UserController(
    private val userService: UserService
) {

}