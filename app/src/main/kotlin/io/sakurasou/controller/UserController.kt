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
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.service.user.UserService
import io.sakurasou.service.user.UserServiceImpl

/**
 * @author ShiinaKin
 * 2024/9/5 15:35
 */

fun Route.userRoute(userService: UserService) {
    val userController = UserController(userService)
    route("user", {
        protected = true
    }) {
        route("{userId}", {
            request {
                pathParameter<Long>("userId") {
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
}

class UserController(
    private val userService: UserService
) {

}