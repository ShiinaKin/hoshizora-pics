package io.sakurasou.hoshizora.controller

import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.routing.route
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.sakurasou.hoshizora.controller.request.GroupInsertRequest
import io.sakurasou.hoshizora.controller.request.GroupPatchRequest
import io.sakurasou.hoshizora.controller.request.GroupPutRequest
import io.sakurasou.hoshizora.service.group.GroupService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GroupControllerTest {
    @Test
    fun `group routes delegate to group service`() {
        val groupService = mockk<GroupService>()
        val insertRequest =
            GroupInsertRequest(
                name = "group",
                description = "desc",
                config = ControllerTestFixtures.groupConfig(),
                strategyId = 1L,
                roles = listOf("ROLE_USER"),
            )
        val putRequest =
            GroupPutRequest(
                name = "group",
                description = "desc",
                strategyId = 1L,
                config = ControllerTestFixtures.groupConfig(),
                roles = listOf("ROLE_USER"),
            )
        val patchRequest = GroupPatchRequest(description = "patched")

        coEvery { groupService.saveGroup(insertRequest) } returns Unit
        coEvery { groupService.deleteGroup(3L) } returns Unit
        coEvery { groupService.updateGroup(3L, putRequest) } returns Unit
        coEvery { groupService.patchGroup(3L, patchRequest) } returns Unit
        coEvery { groupService.fetchGroup(any()) } returns ControllerTestFixtures.groupVO()
        coEvery { groupService.pageGroups(any()) } returns ControllerTestFixtures.groupPageResult()

        testControllerApplication(
            routes = {
                route("api") {
                    groupRoute(groupService)
                }
            },
        ) { client ->
            assertEquals(
                HttpStatusCode.OK,
                client.post("/api/group") {
                    contentType(ContentType.Application.Json)
                    setBody(insertRequest)
                }.status,
            )
            assertEquals(HttpStatusCode.OK, client.delete("/api/group/3").status)
            assertEquals(
                HttpStatusCode.OK,
                client.put("/api/group/3") {
                    contentType(ContentType.Application.Json)
                    setBody(putRequest)
                }.status,
            )
            assertEquals(
                HttpStatusCode.OK,
                client.patch("/api/group/3") {
                    contentType(ContentType.Application.Json)
                    setBody(patchRequest)
                }.status,
            )

            val fetchResponse = client.get("/api/group/3")
            val pageResponse = client.get("/api/group/page?page=1&pageSize=20")
            val typeResponse = client.get("/api/group/type")

            assertEquals(HttpStatusCode.OK, fetchResponse.status)
            assertTrue(fetchResponse.compactBody().contains("\"name\":\"group\""))
            assertEquals(HttpStatusCode.OK, pageResponse.status)
            assertTrue(pageResponse.compactBody().contains("\"total\":1"))
            assertEquals(HttpStatusCode.OK, typeResponse.status)
            assertTrue(typeResponse.compactBody().contains("PNG"))

            coVerify(exactly = 1) { groupService.saveGroup(insertRequest) }
            coVerify(exactly = 1) { groupService.deleteGroup(3L) }
            coVerify(exactly = 1) { groupService.updateGroup(3L, putRequest) }
            coVerify(exactly = 1) { groupService.patchGroup(3L, patchRequest) }
            coVerify(exactly = 1) { groupService.fetchGroup(3L) }
            coVerify(exactly = 1) { groupService.fetchGroup(2L) }
            coVerify(exactly = 1) {
                groupService.pageGroups(
                    match {
                        it.page == 1L && it.pageSize == 20
                    },
                )
            }
        }
    }
}
