@file:OptIn(ExperimentalKtorApi::class)

package io.sakurasou.hoshizora.controller

import io.ktor.openapi.jsonSchema
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.ExperimentalKtorApi
import io.sakurasou.hoshizora.constant.REGEX_EMAIL
import io.sakurasou.hoshizora.constant.REGEX_PASSWORD
import io.sakurasou.hoshizora.constant.REGEX_USERNAME
import io.sakurasou.hoshizora.controller.request.UserInsertRequest
import io.sakurasou.hoshizora.controller.request.UserLoginRequest
import io.sakurasou.hoshizora.extension.success
import io.sakurasou.hoshizora.extension.successResponse
import io.sakurasou.hoshizora.extension.transparentRoute

/**
 * @author Shiina Kin
 * 2024/9/12 10:14
 */
fun Route.authRoute(
    authService: io.sakurasou.hoshizora.service.auth.AuthService,
    userService: io.sakurasou.hoshizora.service.user.UserService,
) {
    val authController = AuthController(authService, userService)
    route("user") {
        login(authController)
        signup(authController)
    }.describe {
        tag("Auth")
    }
}

private fun Route.login(authController: AuthController) {
    transparentRoute {
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
        post("login") {
            val loginRequest = call.receive<UserLoginRequest>()
            val token = authController.handleLogin(loginRequest)
            call.success(mapOf("token" to token))
        }.describe {
            requestBody {
                required = true
                schema = jsonSchema<UserLoginRequest>()
            }
            responses {
                successResponse<Map<String, String>>()
            }
        }
    }
}

private fun Route.signup(authController: AuthController) {
    transparentRoute {
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
        post("signup") {
            val userInsertRequest = call.receive<UserInsertRequest>()
            authController.handleSignup(userInsertRequest)
            call.success()
        }.describe {
            requestBody {
                required = true
                schema = jsonSchema<UserInsertRequest>()
            }
            responses {
                successResponse<Unit>()
            }
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
