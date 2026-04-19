package io.sakurasou.hoshizora.controller

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.route
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.sakurasou.hoshizora.controller.vo.PermissionVO
import io.sakurasou.hoshizora.service.permission.PermissionService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PermissionControllerTest {
    @Test
    fun `fetch all permissions returns service result`() {
        val permissionService = mockk<PermissionService>()

        coEvery { permissionService.fetchAllPermissions() } returns
            listOf(PermissionVO(name = "permission:read", description = "read permissions"))

        testControllerApplication(
            routes = {
                route("api") {
                    permissionRoutes(permissionService)
                }
            },
        ) { client ->
            val response = client.get("/api/permission/all")

            assertEquals(HttpStatusCode.OK, response.status)
            assertTrue(response.compactBody().contains("\"name\":\"permission:read\""))
            coVerify(exactly = 1) { permissionService.fetchAllPermissions() }
        }
    }
}
