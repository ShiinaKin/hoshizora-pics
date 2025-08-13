package io.sakurasou.hoshizora.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.sakurasou.hoshizora.constant.REGEX_EMAIL
import io.sakurasou.hoshizora.constant.REGEX_PASSWORD
import io.sakurasou.hoshizora.constant.REGEX_USERNAME
import io.sakurasou.hoshizora.controller.request.UserInsertRequest
import io.sakurasou.hoshizora.controller.request.UserLoginRequest
import io.sakurasou.hoshizora.controller.vo.CommonResponse
import io.sakurasou.hoshizora.extension.success
import io.swagger.v3.oas.models.media.Schema

/**
 * @author Shiina Kin
 * 2024/9/12 10:14
 */
fun Route.authRoute(
    authService: io.sakurasou.hoshizora.service.auth.AuthService,
    userService: io.sakurasou.hoshizora.service.user.UserService,
) {
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
    route {
        install(RequestValidation) {
            validate<UserLoginRequest> { loginRequest ->
                if (loginRequest.username.isBlank()) {
                    ValidationResult.Invalid("username is required")
                } else if (loginRequest.password.isBlank()) {
                    ValidationResult.Invalid("password is required")
                } else {
                    ValidationResult.Valid
                }
            }
        }
        post("login", {
            request {
                body<UserLoginRequest> {
                    required = true
                }
            }
            response {
                HttpStatusCode.OK to {
                    body(
                        Schema<CommonResponse<Map<String, String>>>().apply {
                            title = "CommonResponseLoginResponse"
                            type = "object"
                            addProperty("code", Schema<Int>().apply { type = "integer" })
                            addProperty("message", Schema<String>().apply { type = "string" })
                            addProperty(
                                "data",
                                Schema<Map<String, String>>().apply {
                                    type = "object"
                                    additionalProperties =
                                        Schema<String>().apply {
                                            type = "string"
                                        }
                                },
                            )
                            addProperty("isSuccessful", Schema<Boolean>().apply { type = "boolean" })
                        },
                    )
                }
            }
        }) {
            val loginRequest = call.receive<UserLoginRequest>()
            val token = authController.handleLogin(loginRequest)
            call.success(mapOf("token" to token))
        }
    }
}

private fun Route.signup(authController: AuthController) {
    route {
        install(RequestValidation) {
            validate<UserInsertRequest> { userInsertRequest ->
                if (!userInsertRequest.username.matches(Regex(REGEX_USERNAME))) {
                    ValidationResult.Invalid("username is invalid")
                } else if (!userInsertRequest.password.matches(Regex(REGEX_PASSWORD))) {
                    ValidationResult.Invalid("password is invalid")
                } else if (!userInsertRequest.email.matches(Regex(REGEX_EMAIL))) {
                    ValidationResult.Invalid("email is invalid")
                } else {
                    ValidationResult.Valid
                }
            }
        }
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
}

class AuthController(
    private val authService: io.sakurasou.hoshizora.service.auth.AuthService,
    private val userService: io.sakurasou.hoshizora.service.user.UserService,
) {
    suspend fun handleLogin(loginRequest: UserLoginRequest): String = authService.login(loginRequest)

    suspend fun handleSignup(userInsertRequest: UserInsertRequest) {
        userService.saveUser(userInsertRequest)
    }
}
