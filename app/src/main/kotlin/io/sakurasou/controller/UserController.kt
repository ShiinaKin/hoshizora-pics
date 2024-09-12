package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.sakurasou.controller.request.UserInsertRequest
import io.sakurasou.controller.request.UserLoginRequest
import io.sakurasou.controller.request.UserPatchRequest
import io.sakurasou.controller.request.pageRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.UserPageVO
import io.sakurasou.controller.vo.UserVO

/**
 * @author ShiinaKin
 * 2024/9/5 15:35
 */

fun Route.userRoute() {
    route("user", {
        protected = true
    }) {
        post("login", {
            protected = false
            request {
                body<UserInsertRequest> {
                    required = true
                }
            }
        }) {
            TODO()
        }
        post("signup", {
            protected = false
            request {
                body<UserLoginRequest> {
                    required = true
                }
            }
        }) {
            TODO()
        }
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
            }) {
                TODO()
            }
            patch({
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
            }) {
                TODO()
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

object UserController {
}