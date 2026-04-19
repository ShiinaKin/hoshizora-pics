package io.sakurasou.hoshizora.controller

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.route
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.sakurasou.hoshizora.service.system.SystemService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SystemControllerTest {
    @Test
    fun `system routes return statistics and overview`() {
        val systemService = mockk<SystemService>()

        coEvery { systemService.fetchSystemStatistics() } returns ControllerTestFixtures.systemStatistics()
        coEvery { systemService.fetchSystemOverview() } returns ControllerTestFixtures.systemOverview()

        testControllerApplication(
            routes = {
                route("api") {
                    systemRoute(systemService)
                }
            },
        ) { client ->
            val statisticsResponse = client.get("/api/system/statistics")
            val overviewResponse = client.get("/api/system/overview")

            assertEquals(HttpStatusCode.OK, statisticsResponse.status)
            assertTrue(statisticsResponse.compactBody().contains("\"totalImageCount\":2"))
            assertEquals(HttpStatusCode.OK, overviewResponse.status)
            assertTrue(overviewResponse.compactBody().contains("\"version\":\"1.0.0\""))
            coVerify(exactly = 1) { systemService.fetchSystemStatistics() }
            coVerify(exactly = 1) { systemService.fetchSystemOverview() }
        }
    }
}
