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
import io.sakurasou.hoshizora.controller.request.AlbumManageInsertRequest
import io.sakurasou.hoshizora.controller.request.AlbumManagePatchRequest
import io.sakurasou.hoshizora.controller.request.AlbumSelfInsertRequest
import io.sakurasou.hoshizora.controller.request.AlbumSelfPatchRequest
import io.sakurasou.hoshizora.service.album.AlbumService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AlbumControllerTest {
    @Test
    fun `self album routes delegate with current user id`() {
        val albumService = mockk<AlbumService>()
        val insertRequest = AlbumSelfInsertRequest(name = "album", description = "desc")
        val patchRequest = AlbumSelfPatchRequest(name = "album-new")

        coEvery { albumService.saveSelf(1L, insertRequest) } returns Unit
        coEvery { albumService.deleteSelf(1L, 10L) } returns Unit
        coEvery { albumService.patchSelf(1L, 10L, patchRequest) } returns Unit
        coEvery { albumService.fetchSelf(1L, 10L) } returns ControllerTestFixtures.albumVO()
        coEvery { albumService.pageSelf(1L, any()) } returns ControllerTestFixtures.albumPageResult()

        testControllerApplication(
            routes = {
                route("api") {
                    albumRoute(albumService)
                }
            },
        ) { client ->
            assertEquals(
                HttpStatusCode.OK,
                client.post("/api/album") {
                    contentType(ContentType.Application.Json)
                    setBody(insertRequest)
                }.status,
            )
            assertEquals(HttpStatusCode.OK, client.delete("/api/album/10").status)
            assertEquals(
                HttpStatusCode.OK,
                client.patch("/api/album/10") {
                    contentType(ContentType.Application.Json)
                    setBody(patchRequest)
                }.status,
            )

            val fetchResponse = client.get("/api/album/10")
            val pageResponse = client.get("/api/album/page?page=1&pageSize=20&order=asc&orderBy=name&albumName=album")

            assertEquals(HttpStatusCode.OK, fetchResponse.status)
            assertTrue(fetchResponse.compactBody().contains("\"name\":\"album\""))
            assertEquals(HttpStatusCode.OK, pageResponse.status)
            assertTrue(pageResponse.compactBody().contains("\"total\":1"))

            coVerify(exactly = 1) { albumService.saveSelf(1L, insertRequest) }
            coVerify(exactly = 1) { albumService.deleteSelf(1L, 10L) }
            coVerify(exactly = 1) { albumService.patchSelf(1L, 10L, patchRequest) }
            coVerify(exactly = 1) { albumService.fetchSelf(1L, 10L) }
            coVerify(exactly = 1) {
                albumService.pageSelf(
                    1L,
                    match {
                        it.page == 1L &&
                            it.pageSize == 20 &&
                            it.order == "asc" &&
                            it.orderBy == "name" &&
                            it.additionalCondition == mapOf("albumName" to "album")
                    },
                )
            }
        }
    }

    @Test
    fun `manage album routes delegate to album service`() {
        val albumService = mockk<AlbumService>()
        val insertRequest = AlbumManageInsertRequest(userId = 1L, name = "album", description = "desc")
        val patchRequest = AlbumManagePatchRequest(userId = 1L, name = "album-new")

        coEvery { albumService.saveAlbum(insertRequest) } returns Unit
        coEvery { albumService.deleteAlbum(10L) } returns Unit
        coEvery { albumService.patchAlbum(10L, patchRequest) } returns Unit
        coEvery { albumService.fetchAlbum(10L) } returns ControllerTestFixtures.albumManageVO()
        coEvery { albumService.pageAlbum(any()) } returns ControllerTestFixtures.albumManagePageResult()

        testControllerApplication(
            routes = {
                route("api") {
                    albumRoute(albumService)
                }
            },
        ) { client ->
            assertEquals(
                HttpStatusCode.OK,
                client.post("/api/album/manage") {
                    contentType(ContentType.Application.Json)
                    setBody(insertRequest)
                }.status,
            )
            assertEquals(HttpStatusCode.OK, client.delete("/api/album/manage/10").status)
            assertEquals(
                HttpStatusCode.OK,
                client.patch("/api/album/manage/10") {
                    contentType(ContentType.Application.Json)
                    setBody(patchRequest)
                }.status,
            )

            val fetchResponse = client.get("/api/album/manage/10")
            val pageResponse = client.get("/api/album/manage/page?page=1&pageSize=20&userId=1&albumName=album")

            assertEquals(HttpStatusCode.OK, fetchResponse.status)
            assertTrue(fetchResponse.compactBody().contains("\"username\":\"tester\""))
            assertEquals(HttpStatusCode.OK, pageResponse.status)
            assertTrue(pageResponse.compactBody().contains("\"total\":1"))

            coVerify(exactly = 1) { albumService.saveAlbum(insertRequest) }
            coVerify(exactly = 1) { albumService.deleteAlbum(10L) }
            coVerify(exactly = 1) { albumService.patchAlbum(10L, patchRequest) }
            coVerify(exactly = 1) { albumService.fetchAlbum(10L) }
            coVerify(exactly = 1) {
                albumService.pageAlbum(
                    match {
                        it.page == 1L &&
                            it.pageSize == 20 &&
                            it.additionalCondition == mapOf("userId" to "1", "albumName" to "album")
                    },
                )
            }
        }
    }
}
