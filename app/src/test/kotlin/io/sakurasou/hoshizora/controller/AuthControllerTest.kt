package io.sakurasou.hoshizora.controller

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.routing.route
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.sakurasou.hoshizora.controller.request.UserInsertRequest
import io.sakurasou.hoshizora.controller.request.UserLoginRequest
import io.sakurasou.hoshizora.service.auth.AuthService
import io.sakurasou.hoshizora.service.user.UserService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthControllerTest {
    @Test
    fun `login returns token response`() {
        val authService = mockk<AuthService>()
        val userService = mockk<UserService>(relaxed = true)
        val request = UserLoginRequest(username = "tester1", password = "Passw0rd!")

        coEvery { authService.login(request) } returns "jwt-token"

        testControllerApplication(
            routes = {
                route("api") {
                    authRoute(authService, userService)
                }
            },
        ) { client ->
            val response =
                client.post("/api/user/login") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.compactBody().contains("\"token\":\"jwt-token\""))
            coVerify(exactly = 1) { authService.login(request) }
        }
    }

    @Test
    fun `signup delegates to user service`() {
        val authService = mockk<AuthService>()
        val userService = mockk<UserService>()
        val request =
            UserInsertRequest(
                username = "tester1",
                email = "tester@example.com",
                password = "Passw0rd!",
            )

        coEvery { userService.saveUser(request) } returns Unit

        testControllerApplication(
            routes = {
                route("api") {
                    authRoute(authService, userService)
                }
            },
        ) { client ->
            val response =
                client.post("/api/user/signup") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.compactBody().contains("\"isSuccessful\":true"))
            coVerify(exactly = 1) { userService.saveUser(request) }
        }
    }

    @Test
    fun `login validation rejects blank username before service call`() {
        val authService = mockk<AuthService>()
        val userService = mockk<UserService>(relaxed = true)
        val request = UserLoginRequest(username = "", password = "Passw0rd!")

        testControllerApplication(
            routes = {
                route("api") {
                    authRoute(authService, userService)
                }
            },
        ) { client ->
            val response =
                client.post("/api/user/login") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

            assertEquals(HttpStatusCode.OK, response.status)
            assertFalse(response.compactBody().contains("\"isSuccessful\":true"))
            coVerify(exactly = 0) { authService.login(any()) }
        }
    }
}
