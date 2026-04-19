package io.sakurasou.hoshizora.controller

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.routing.route
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.sakurasou.hoshizora.controller.request.SiteInitRequest
import io.sakurasou.hoshizora.model.dto.ImageFileDTO
import io.sakurasou.hoshizora.service.common.CommonService
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommonControllerTest {
    @Test
    fun `site init delegates to common service`() {
        val commonService = mockk<CommonService>()
        val request =
            SiteInitRequest(
                username = "tester1",
                password = "Passw0rd!",
                email = "tester@example.com",
                siteExternalUrl = "https://example.com",
                siteTitle = "Hoshizora",
                siteSubtitle = "Pics",
                siteDescription = "Images",
            )

        coEvery { commonService.initSite(request) } returns Unit

        testControllerApplication(
            routes = {
                route("api") {
                    siteInitRoute(commonService)
                }
            },
        ) { client ->
            val response =
                client.post("/api/site/init") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.compactBody().contains("\"isSuccessful\":true"))
            coVerify(exactly = 1) { commonService.initSite(request) }
        }
    }

    @Test
    fun `site setting returns common setting response`() {
        val commonService = mockk<CommonService>()
        val siteSetting = ControllerTestFixtures.commonSiteSetting()

        coEvery { commonService.fetchCommonSiteSetting() } returns siteSetting

        testControllerApplication(
            routes = {
                route("api") {
                    commonSiteSettingRoute(commonService)
                }
            },
        ) { client ->
            val response = client.get("/api/site/setting")

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.compactBody().contains("\"siteTitle\":\"Hoshizora\""))
            coVerify(exactly = 1) { commonService.fetchCommonSiteSetting() }
        }
    }

    @Test
    fun `random image returns bytes from service`() {
        val commonService = mockk<CommonService>()
        val bytes = byteArrayOf(1, 2, 3)

        coEvery { commonService.fetchRandomImage() } returns ImageFileDTO(bytes = bytes)

        testControllerApplication(
            routes = {
                commonRoute(commonService)
            },
        ) { client ->
            val response = client.get("/random?id=test")

            assertEquals(HttpStatusCode.OK, response.status)
            assertContentEquals(bytes, response.bodyAsBytes())
            coVerify(exactly = 1) { commonService.fetchRandomImage() }
        }
    }

    @Test
    fun `anonymous image returns bytes from service`() {
        val commonService = mockk<CommonService>()
        val imageUniqueName = "1234567890abcdef1234567890abcdef"
        val bytes = byteArrayOf(4, 5, 6)

        coEvery { commonService.fetchImage(imageUniqueName) } returns ImageFileDTO(bytes = bytes)

        testControllerApplication(
            routes = {
                commonRoute(commonService)
            },
        ) { client ->
            val response = client.get("/s/$imageUniqueName")

            assertEquals(HttpStatusCode.OK, response.status)
            assertContentEquals(bytes, response.bodyAsBytes())
            coVerify(exactly = 1) { commonService.fetchImage(imageUniqueName) }
        }
    }
}
