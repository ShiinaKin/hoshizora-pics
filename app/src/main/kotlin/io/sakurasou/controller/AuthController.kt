package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.sakurasou.controller.request.UserInsertRequest
import io.sakurasou.controller.request.UserLoginRequest
import io.sakurasou.controller.vo.CommonResponse
import io.sakurasou.extension.success
import io.sakurasou.service.auth.AuthService
import io.sakurasou.service.user.UserService
import io.swagger.v3.oas.models.media.Schema

/**
 * @author Shiina Kin
 * 2024/9/12 10:14
 */
fun Route.authRoute(authService: AuthService, userService: UserService) {
    val authController = AuthController(authService, userService)
    route("user", {
        protected = false
        tags("Auth")
    }) {
        login(authController)
        signup(authController)
    }
}

private fun Route.login(authController: AuthController) {
    post("login", {
        request {
            body<UserLoginRequest> {
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                body(Schema<CommonResponse<Map<String, String>>>().apply {
                    title = "CommonResponseLoginResponse"
                    type = "object"
                    addProperty("code", Schema<Int>().apply { type = "integer" })
                    addProperty("message", Schema<String>().apply { type = "string" })
                    addProperty("data", Schema<Map<String, String>>().apply {
                        type = "object"
                        additionalProperties = Schema<String>().apply {
                            type = "string"
                        }
                    })
                    addProperty("isSuccessful", Schema<Boolean>().apply { type = "boolean" })
                })
            }
        }
    }) {
        val loginRequest = call.receive<UserLoginRequest>()
        val token = authController.handleLogin(loginRequest)
        call.success(mapOf("token" to token))
    }
}

private fun Route.signup(authController: AuthController) {
    post("signup", {
        protected = false
        request {
            body<UserInsertRequest> {
                required = true
            }
        }
        response {
            HttpStatusCode.OK to {
                body<CommonResponse<Unit>> { }
            }
        }
    }) {
        val userInsertRequest = call.receive<UserInsertRequest>()
        authController.handleSignup(userInsertRequest)
        call.success()
    }
}

class AuthController(
    private val authService: AuthService,
    private val userService: UserService
) {
    suspend fun handleLogin(loginRequest: UserLoginRequest): String {
        return authService.login(loginRequest)
    }

    suspend fun handleSignup(userInsertRequest: UserInsertRequest) {
        userService.saveUser(userInsertRequest)
    }
}