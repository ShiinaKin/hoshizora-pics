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
import io.sakurasou.hoshizora.controller.request.PersonalAccessTokenInsertRequest
import io.sakurasou.hoshizora.controller.request.PersonalAccessTokenPatchRequest
import io.sakurasou.hoshizora.service.personalAccessToken.PersonalAccessTokenService
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersonalAccessTokenControllerTest {
    @Test
    fun `personal access token routes delegate to service with current user id`() {
        val patService = mockk<PersonalAccessTokenService>()
        val insertRequest =
            PersonalAccessTokenInsertRequest(
                name = "token",
                description = "desc",
                expireTime = LocalDateTime(2030, 1, 1, 0, 0),
                permissions = listOf("user:read:self"),
            )
        val patchRequest = PersonalAccessTokenPatchRequest(name = "token-new")

        coEvery { patService.savePAT(1L, insertRequest) } returns "pat-token"
        coEvery { patService.deletePAT(1L, 5L) } returns Unit
        coEvery { patService.patchPAT(1L, 5L, patchRequest) } returns Unit
        coEvery { patService.pagePAT(1L, any()) } returns ControllerTestFixtures.patPageResult()

        testControllerApplication(
            routes = {
                route("api") {
                    personalAccessTokenRoute(patService)
                }
            },
        ) { client ->
            val insertResponse =
                client.post("/api/personal-access-token") {
                    contentType(ContentType.Application.Json)
                    setBody(insertRequest)
                }
            assertEquals(HttpStatusCode.OK, insertResponse.status)
            assertTrue(insertResponse.compactBody().contains("\"data\":\"pat-token\""))

            assertEquals(HttpStatusCode.OK, client.delete("/api/personal-access-token/5").status)
            assertEquals(
                HttpStatusCode.OK,
                client.patch("/api/personal-access-token/5") {
                    contentType(ContentType.Application.Json)
                    setBody(patchRequest)
                }.status,
            )

            val pageResponse = client.get("/api/personal-access-token/page?page=1&pageSize=20&isExpired=false")
            assertEquals(HttpStatusCode.OK, pageResponse.status)
            assertTrue(pageResponse.compactBody().contains("\"total\":1"))

            coVerify(exactly = 1) { patService.savePAT(1L, insertRequest) }
            coVerify(exactly = 1) { patService.deletePAT(1L, 5L) }
            coVerify(exactly = 1) { patService.patchPAT(1L, 5L, patchRequest) }
            coVerify(exactly = 1) {
                patService.pagePAT(
                    1L,
                    match {
                        it.page == 1L &&
                            it.pageSize == 20 &&
                            it.additionalCondition == mapOf("isExpired" to "false")
                    },
                )
            }
        }
    }
}
