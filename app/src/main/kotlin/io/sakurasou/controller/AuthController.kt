package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.sakurasou.controller.request.UserInsertRequest
import io.sakurasou.controller.request.UserLoginRequest
import io.sakurasou.extension.success
import io.sakurasou.service.auth.AuthService
import io.sakurasou.service.user.UserService

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
    }) {
        val loginRequest = call.receive<UserLoginRequest>()
        val token = authController.handleLogin(loginRequest)
        call.respond(mapOf("token" to token))
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