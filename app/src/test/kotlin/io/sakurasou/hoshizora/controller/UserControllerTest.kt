package io.sakurasou.hoshizora.controller

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
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
import io.sakurasou.hoshizora.controller.request.UserManageInsertRequest
import io.sakurasou.hoshizora.controller.request.UserManagePatchRequest
import io.sakurasou.hoshizora.controller.request.UserSelfPatchRequest
import io.sakurasou.hoshizora.service.user.UserService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserControllerTest {
    @Test
    fun `self user routes delegate with current user id`() {
        val userService = mockk<UserService>()
        val patchRequest = UserSelfPatchRequest(email = "new@example.com")

        coEvery { userService.patchSelf(1L, patchRequest) } returns Unit
        coEvery { userService.fetchUser(1L) } returns ControllerTestFixtures.userVO()

        testControllerApplication(
            routes = {
                route("api") {
                    userRoute(userService)
                }
            },
        ) { client ->
            val patchResponse =
                client.patch("/api/user/self") {
                    contentType(ContentType.Application.Json)
                    setBody(patchRequest)
                }
            val fetchResponse = client.get("/api/user/self")

            assertEquals(HttpStatusCode.OK, patchResponse.status)
            assertTrue(patchResponse.compactBody().contains("\"isSuccessful\":true"))
            assertEquals(HttpStatusCode.OK, fetchResponse.status)
            assertTrue(fetchResponse.compactBody().contains("\"username\":\"tester\""))
            coVerify(exactly = 1) { userService.patchSelf(1L, patchRequest) }
            coVerify(exactly = 1) { userService.fetchUser(1L) }
        }
    }

    @Test
    fun `manage user routes delegate to user service`() {
        val userService = mockk<UserService>()
        val insertRequest =
            UserManageInsertRequest(
                groupId = 2L,
                username = "tester2",
                password = "Passw0rd!",
                email = "tester2@example.com",
                isDefaultImagePrivate = true,
            )
        val patchRequest = UserManagePatchRequest(groupId = 2L, email = "new@example.com")

        coEvery { userService.saveUserManually(insertRequest) } returns Unit
        coEvery { userService.deleteUser(3L) } returns Unit
        coEvery { userService.patchUser(3L, patchRequest) } returns Unit
        coEvery { userService.fetchUser(3L) } returns ControllerTestFixtures.userVO()
        coEvery { userService.pageUsers(any()) } returns ControllerTestFixtures.userPageResult()
        coEvery { userService.banUser(3L) } returns Unit
        coEvery { userService.unbanUser(3L) } returns Unit

        testControllerApplication(
            routes = {
                route("api") {
                    userRoute(userService)
                }
            },
        ) { client ->
            assertEquals(
                HttpStatusCode.OK,
                client.post("/api/user/manage") {
                    contentType(ContentType.Application.Json)
                    setBody(insertRequest)
                }.status,
            )
            assertEquals(HttpStatusCode.OK, client.delete("/api/user/manage/3").status)
            assertEquals(
                HttpStatusCode.OK,
                client.patch("/api/user/manage/3") {
                    contentType(ContentType.Application.Json)
                    setBody(patchRequest)
                }.status,
            )

            val fetchResponse = client.get("/api/user/manage/3")
            val pageResponse = client.get("/api/user/manage/page?page=1&pageSize=20&isBanned=true&username=tester")
            val banResponse = client.patch("/api/user/ban/3")
            val unbanResponse = client.patch("/api/user/unban/3")

            assertEquals(HttpStatusCode.OK, fetchResponse.status)
            assertTrue(fetchResponse.compactBody().contains("\"username\":\"tester\""))
            assertEquals(HttpStatusCode.OK, pageResponse.status)
            assertTrue(pageResponse.compactBody().contains("\"total\":1"))
            assertEquals(HttpStatusCode.OK, banResponse.status)
            assertEquals(HttpStatusCode.OK, unbanResponse.status)

            coVerify(exactly = 1) { userService.saveUserManually(insertRequest) }
            coVerify(exactly = 1) { userService.deleteUser(3L) }
            coVerify(exactly = 1) { userService.patchUser(3L, patchRequest) }
            coVerify(exactly = 1) { userService.fetchUser(3L) }
            coVerify(exactly = 1) { userService.banUser(3L) }
            coVerify(exactly = 1) { userService.unbanUser(3L) }
            coVerify(exactly = 1) {
                userService.pageUsers(
                    match {
                        it.page == 1L &&
                            it.pageSize == 20 &&
                            it.additionalCondition == mapOf("isBanned" to "true", "username" to "tester")
                    },
                )
            }
        }
    }
}
