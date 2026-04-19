package io.sakurasou.hoshizora.controller

import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.routing.route
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.sakurasou.hoshizora.controller.request.SiteSettingPatchRequest
import io.sakurasou.hoshizora.controller.request.SystemSettingPatchRequest
import io.sakurasou.hoshizora.model.setting.SiteSetting
import io.sakurasou.hoshizora.model.setting.SystemSetting
import io.sakurasou.hoshizora.service.setting.SettingService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingControllerTest {
    @Test
    fun `setting fetch routes return site and system settings`() {
        val settingService = mockk<SettingService>()

        coEvery { settingService.getSiteSetting() } returns
            SiteSetting(
                siteExternalUrl = "https://example.com",
                siteTitle = "Hoshizora",
                siteSubtitle = "Pics",
                siteDescription = "Images",
            )
        coEvery { settingService.getSystemSetting() } returns
            SystemSetting(
                defaultGroupId = 2L,
                allowSignup = true,
                allowRandomFetch = false,
            )

        testControllerApplication(
            routes = {
                route("api") {
                    settingRoute(settingService)
                }
            },
        ) { client ->
            val siteResponse = client.get("/api/setting/site")
            val systemResponse = client.get("/api/setting/system")

            assertEquals(HttpStatusCode.OK, siteResponse.status)
            assertTrue(siteResponse.compactBody().contains("\"siteTitle\":\"Hoshizora\""))
            assertEquals(HttpStatusCode.OK, systemResponse.status)
            assertTrue(systemResponse.compactBody().contains("\"defaultGroupId\":2"))
            coVerify(exactly = 1) { settingService.getSiteSetting() }
            coVerify(exactly = 1) { settingService.getSystemSetting() }
        }
    }

    @Test
    fun `setting patch routes delegate to setting service`() {
        val settingService = mockk<SettingService>()
        val sitePatch = SiteSettingPatchRequest(siteTitle = "New Title")
        val systemPatch = SystemSettingPatchRequest(defaultGroupId = 3L, allowSignup = false)

        coEvery { settingService.updateSiteSetting(sitePatch) } returns Unit
        coEvery { settingService.updateSystemSetting(systemPatch) } returns Unit

        testControllerApplication(
            routes = {
                route("api") {
                    settingRoute(settingService)
                }
            },
        ) { client ->
            val siteResponse =
                client.patch("/api/setting/site") {
                    contentType(ContentType.Application.Json)
                    setBody(sitePatch)
                }
            val systemResponse =
                client.patch("/api/setting/system") {
                    contentType(ContentType.Application.Json)
                    setBody(systemPatch)
                }

            assertEquals(HttpStatusCode.OK, siteResponse.status)
            assertTrue(siteResponse.compactBody().contains("\"isSuccessful\":true"))
            assertEquals(HttpStatusCode.OK, systemResponse.status)
            assertTrue(systemResponse.compactBody().contains("\"isSuccessful\":true"))
            coVerify(exactly = 1) { settingService.updateSiteSetting(sitePatch) }
            coVerify(exactly = 1) { settingService.updateSystemSetting(systemPatch) }
        }
    }
}
