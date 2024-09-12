package io.sakurasou.controller

import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.sakurasou.controller.request.UserInsertRequest
import io.sakurasou.controller.request.UserLoginRequest
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.service.auth.AuthService
import io.sakurasou.service.auth.AuthServiceImpl

/**
 * @author Shiina Kin
 * 2024/9/12 10:14
 */
fun Route.authRoute(userDao: UserDao, relationDao: RelationDao) {
    val authService = AuthServiceImpl(userDao, relationDao)
    val authController = AuthController(authService)
    route("user", {
        protected = false
    }) {
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
        }
    }
}

class AuthController(
    private val authService: AuthService
) {
    suspend fun handleLogin(loginRequest: UserLoginRequest): String {
        return authService.login(loginRequest)
    }

    suspend fun handleSignup(userInsertRequest: UserInsertRequest) {
        authService.saveUser(userInsertRequest)
    }
}