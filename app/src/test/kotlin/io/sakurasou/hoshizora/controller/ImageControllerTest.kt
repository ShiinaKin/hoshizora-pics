package io.sakurasou.hoshizora.controller

import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.routing.route
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.sakurasou.hoshizora.controller.request.ImageManagePatchRequest
import io.sakurasou.hoshizora.controller.request.ImagePatchRequest
import io.sakurasou.hoshizora.model.dto.ImageFileDTO
import io.sakurasou.hoshizora.service.image.ImageService
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImageControllerTest {
    @Test
    fun `self image routes delegate with current user id`() {
        val imageService = mockk<ImageService>()
        val uploadBytes = byteArrayOf(1, 2, 3)
        val fileBytes = byteArrayOf(4, 5, 6)
        val thumbnailBytes = byteArrayOf(7, 8, 9)
        val patchRequest = ImagePatchRequest(displayName = "image-new")

        coEvery {
            imageService.saveImage(
                1L,
                match {
                    it.name == "image.png" &&
                        it.mimeType == "image/png" &&
                        it.size == uploadBytes.size.toLong() &&
                        it.bytes.contentEquals(uploadBytes)
                },
            )
        } returns "external-id"
        coEvery { imageService.deleteSelfImage(1L, 9L) } returns Unit
        coEvery { imageService.patchSelfImage(1L, 9L, patchRequest) } returns Unit
        coEvery { imageService.fetchSelfImageInfo(1L, 9L) } returns ControllerTestFixtures.imageVO()
        coEvery { imageService.fetchSelfImageFile(1L, 9L) } returns ImageFileDTO(bytes = fileBytes)
        coEvery { imageService.fetchSelfImageThumbnailFile(1L, 9L) } returns ImageFileDTO(bytes = thumbnailBytes)
        coEvery { imageService.pageSelfImage(1L, any()) } returns ControllerTestFixtures.imagePageResult()

        testControllerApplication(
            routes = {
                route("api") {
                    imageRoute(imageService)
                }
            },
        ) { client ->
            val uploadResponse =
                client.post("/api/image") {
                    setBody(imageMultipart(uploadBytes))
                }
            assertEquals(HttpStatusCode.OK, uploadResponse.status)
            assertTrue(uploadResponse.compactBody().contains("\"data\":\"external-id\""))

            assertEquals(HttpStatusCode.OK, client.delete("/api/image/9").status)
            assertEquals(
                HttpStatusCode.OK,
                client.patch("/api/image/9") {
                    contentType(ContentType.Application.Json)
                    setBody(patchRequest)
                }.status,
            )

            val infoResponse = client.get("/api/image/9/info")
            val fileResponse = client.get("/api/image/9/file")
            val thumbnailResponse = client.get("/api/image/9/thumbnail")
            val pageResponse =
                client.get("/api/image/page?page=1&pageSize=20&albumId=10&isPrivate=false&search=image")

            assertEquals(HttpStatusCode.OK, infoResponse.status)
            assertTrue(infoResponse.compactBody().contains("\"displayName\":\"image\""))
            assertEquals(HttpStatusCode.OK, fileResponse.status)
            assertContentEquals(fileBytes, fileResponse.bodyAsBytes())
            assertEquals(HttpStatusCode.OK, thumbnailResponse.status)
            assertContentEquals(thumbnailBytes, thumbnailResponse.bodyAsBytes())
            assertEquals(HttpStatusCode.OK, pageResponse.status)
            assertTrue(pageResponse.compactBody().contains("\"total\":1"))

            coVerify(exactly = 1) { imageService.deleteSelfImage(1L, 9L) }
            coVerify(exactly = 1) { imageService.patchSelfImage(1L, 9L, patchRequest) }
            coVerify(exactly = 1) { imageService.fetchSelfImageInfo(1L, 9L) }
            coVerify(exactly = 1) { imageService.fetchSelfImageFile(1L, 9L) }
            coVerify(exactly = 1) { imageService.fetchSelfImageThumbnailFile(1L, 9L) }
            coVerify(exactly = 1) {
                imageService.pageSelfImage(
                    1L,
                    match {
                        it.page == 1L &&
                            it.pageSize == 20 &&
                            it.additionalCondition ==
                            mapOf("albumId" to "10", "isPrivate" to "false", "search" to "image")
                    },
                )
            }
        }
    }

    @Test
    fun `manage image routes delegate to image service`() {
        val imageService = mockk<ImageService>()
        val fileBytes = byteArrayOf(10, 11, 12)
        val thumbnailBytes = byteArrayOf(13, 14, 15)
        val patchRequest = ImageManagePatchRequest(userId = 1L, displayName = "image-new")

        coEvery { imageService.deleteImage(9L) } returns Unit
        coEvery { imageService.patchImage(9L, patchRequest) } returns Unit
        coEvery { imageService.fetchImageInfo(9L) } returns ControllerTestFixtures.imageManageVO()
        coEvery { imageService.fetchImageFile(9L) } returns ImageFileDTO(bytes = fileBytes)
        coEvery { imageService.fetchImageThumbnailFile(9L) } returns ImageFileDTO(bytes = thumbnailBytes)
        coEvery { imageService.pageImage(any()) } returns ControllerTestFixtures.imageManagePageResult()

        testControllerApplication(
            routes = {
                route("api") {
                    imageRoute(imageService)
                }
            },
        ) { client ->
            assertEquals(HttpStatusCode.OK, client.delete("/api/image/manage/9").status)
            assertEquals(
                HttpStatusCode.OK,
                client.patch("/api/image/manage/9") {
                    contentType(ContentType.Application.Json)
                    setBody(patchRequest)
                }.status,
            )

            val infoResponse = client.get("/api/image/manage/9/info")
            val fileResponse = client.get("/api/image/manage/9/file")
            val thumbnailResponse = client.get("/api/image/manage/9/thumbnail")
            val pageResponse =
                client.get("/api/image/manage/page?page=1&pageSize=20&userId=1&albumId=10&isPrivate=false&search=image")

            assertEquals(HttpStatusCode.OK, infoResponse.status)
            assertTrue(infoResponse.compactBody().contains("\"groupName\":\"group\""))
            assertEquals(HttpStatusCode.OK, fileResponse.status)
            assertContentEquals(fileBytes, fileResponse.bodyAsBytes())
            assertEquals(HttpStatusCode.OK, thumbnailResponse.status)
            assertContentEquals(thumbnailBytes, thumbnailResponse.bodyAsBytes())
            assertEquals(HttpStatusCode.OK, pageResponse.status)
            assertTrue(pageResponse.compactBody().contains("\"total\":1"))

            coVerify(exactly = 1) { imageService.deleteImage(9L) }
            coVerify(exactly = 1) { imageService.patchImage(9L, patchRequest) }
            coVerify(exactly = 1) { imageService.fetchImageInfo(9L) }
            coVerify(exactly = 1) { imageService.fetchImageFile(9L) }
            coVerify(exactly = 1) { imageService.fetchImageThumbnailFile(9L) }
            coVerify(exactly = 1) {
                imageService.pageImage(
                    match {
                        it.page == 1L &&
                            it.pageSize == 20 &&
                            it.additionalCondition ==
                            mapOf(
                                "userId" to "1",
                                "albumId" to "10",
                                "isPrivate" to "false",
                                "search" to "image",
                            )
                    },
                )
            }
        }
    }

    private fun imageMultipart(bytes: ByteArray): MultiPartFormDataContent =
        MultiPartFormDataContent(
            formData {
                append(
                    key = "file",
                    value = bytes,
                    headers =
                        Headers.build {
                            append(HttpHeaders.ContentType, "image/png")
                            append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"image.png\"")
                        },
                )
            },
        )
}
