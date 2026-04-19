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
import io.sakurasou.hoshizora.controller.request.RoleInsertRequest
import io.sakurasou.hoshizora.controller.request.RolePatchRequest
import io.sakurasou.hoshizora.service.role.RoleService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RoleControllerTest {
    @Test
    fun `role routes delegate to role service`() {
        val roleService = mockk<RoleService>()
        val insertRequest =
            RoleInsertRequest(
                name = "ROLE_USER",
                displayName = "User",
                description = "role",
                permissions = listOf("user:read:self"),
            )
        val patchRequest = RolePatchRequest(description = "patched")

        coEvery { roleService.saveRole(insertRequest) } returns Unit
        coEvery { roleService.patchRole("ROLE_USER", patchRequest) } returns Unit
        coEvery { roleService.deleteRole("ROLE_USER") } returns Unit
        coEvery { roleService.fetchRole("ROLE_USER") } returns ControllerTestFixtures.roleVO()
        coEvery { roleService.listRolesWithPermissionsOfUser(2L) } returns listOf(ControllerTestFixtures.roleVO())
        coEvery { roleService.pageRoles(any()) } returns ControllerTestFixtures.rolePageResult()

        testControllerApplication(
            routes = {
                route("api") {
                    roleRoute(roleService)
                }
            },
        ) { client ->
            assertEquals(
                HttpStatusCode.OK,
                client.post("/api/role") {
                    contentType(ContentType.Application.Json)
                    setBody(insertRequest)
                }.status,
            )
            assertEquals(
                HttpStatusCode.OK,
                client.patch("/api/role/ROLE_USER") {
                    contentType(ContentType.Application.Json)
                    setBody(patchRequest)
                }.status,
            )
            assertEquals(HttpStatusCode.OK, client.delete("/api/role/ROLE_USER").status)

            val fetchResponse = client.get("/api/role/ROLE_USER")
            val selfResponse = client.get("/api/role/self")
            val pageResponse = client.get("/api/role/page?page=1&pageSize=20")

            assertEquals(HttpStatusCode.OK, fetchResponse.status)
            assertTrue(fetchResponse.compactBody().contains("\"name\":\"ROLE_USER\""))
            assertEquals(HttpStatusCode.OK, selfResponse.status)
            assertTrue(selfResponse.compactBody().contains("\"permissions\""))
            assertEquals(HttpStatusCode.OK, pageResponse.status)
            assertTrue(pageResponse.compactBody().contains("\"total\":1"))

            coVerify(exactly = 1) { roleService.saveRole(insertRequest) }
            coVerify(exactly = 1) { roleService.patchRole("ROLE_USER", patchRequest) }
            coVerify(exactly = 1) { roleService.deleteRole("ROLE_USER") }
            coVerify(exactly = 1) { roleService.fetchRole("ROLE_USER") }
            coVerify(exactly = 1) { roleService.listRolesWithPermissionsOfUser(2L) }
            coVerify(exactly = 1) {
                roleService.pageRoles(
                    match {
                        it.page == 1L && it.pageSize == 20
                    },
                )
            }
        }
    }
}
