package io.sakurasou.hoshizora.controller

import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.routing.route
import io.mockk.coEvery
import io.mockk.mockk
import io.sakurasou.hoshizora.controller.request.AlbumManageInsertRequest
import io.sakurasou.hoshizora.controller.request.AlbumManagePatchRequest
import io.sakurasou.hoshizora.controller.request.AlbumSelfInsertRequest
import io.sakurasou.hoshizora.controller.request.AlbumSelfPatchRequest
import io.sakurasou.hoshizora.controller.request.GroupInsertRequest
import io.sakurasou.hoshizora.controller.request.GroupPatchRequest
import io.sakurasou.hoshizora.controller.request.GroupPutRequest
import io.sakurasou.hoshizora.controller.request.ImageManagePatchRequest
import io.sakurasou.hoshizora.controller.request.ImagePatchRequest
import io.sakurasou.hoshizora.controller.request.PersonalAccessTokenInsertRequest
import io.sakurasou.hoshizora.controller.request.PersonalAccessTokenPatchRequest
import io.sakurasou.hoshizora.controller.request.RoleInsertRequest
import io.sakurasou.hoshizora.controller.request.RolePatchRequest
import io.sakurasou.hoshizora.controller.request.SiteInitRequest
import io.sakurasou.hoshizora.controller.request.SiteSettingPatchRequest
import io.sakurasou.hoshizora.controller.request.StrategyInsertRequest
import io.sakurasou.hoshizora.controller.request.StrategyPatchRequest
import io.sakurasou.hoshizora.controller.request.SystemSettingPatchRequest
import io.sakurasou.hoshizora.controller.request.UserInsertRequest
import io.sakurasou.hoshizora.controller.request.UserLoginRequest
import io.sakurasou.hoshizora.controller.request.UserManageInsertRequest
import io.sakurasou.hoshizora.controller.request.UserManagePatchRequest
import io.sakurasou.hoshizora.controller.request.UserSelfPatchRequest
import io.sakurasou.hoshizora.model.dto.ImageFileDTO
import io.sakurasou.hoshizora.model.strategy.S3Strategy
import io.sakurasou.hoshizora.model.strategy.WebDavStrategy
import io.sakurasou.hoshizora.service.album.AlbumService
import io.sakurasou.hoshizora.service.auth.AuthService
import io.sakurasou.hoshizora.service.common.CommonService
import io.sakurasou.hoshizora.service.group.GroupService
import io.sakurasou.hoshizora.service.image.ImageService
import io.sakurasou.hoshizora.service.personalAccessToken.PersonalAccessTokenService
import io.sakurasou.hoshizora.service.role.RoleService
import io.sakurasou.hoshizora.service.setting.SettingService
import io.sakurasou.hoshizora.service.strategy.StrategyService
import io.sakurasou.hoshizora.service.user.UserService
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ControllerCoverageGapTest {
    @Test
    fun `auth validation rejects every invalid branch`() {
        val authService = mockk<AuthService>(relaxed = true)
        val userService = mockk<UserService>(relaxed = true)

        testControllerApplication(
            routes = {
                route("api") {
                    authRoute(authService, userService)
                }
            },
        ) { client ->
            assertRejected(
                client.post("/api/user/login") {
                    contentType(ContentType.Application.Json)
                    setBody(UserLoginRequest(username = "tester1", password = ""))
                },
            )
            assertRejected(
                client.post("/api/user/signup") {
                    contentType(ContentType.Application.Json)
                    setBody(UserInsertRequest(username = "bad", email = "tester@example.com", password = "Passw0rd!"))
                },
            )
            assertRejected(
                client.post("/api/user/signup") {
                    contentType(ContentType.Application.Json)
                    setBody(UserInsertRequest(username = "tester1", email = "tester@example.com", password = "bad"))
                },
            )
            assertRejected(
                client.post("/api/user/signup") {
                    contentType(ContentType.Application.Json)
                    setBody(UserInsertRequest(username = "tester1", email = "bad-email", password = "Passw0rd!"))
                },
            )
        }
    }

    @Test
    fun `common validation and remote image branches are covered`() {
        val commonService = mockk<CommonService>()
        val valid =
            SiteInitRequest(
                username = "tester1",
                password = "Passw0rd!",
                email = "tester@example.com",
                siteExternalUrl = "https://example.com",
                siteTitle = "Hoshizora",
                siteSubtitle = "Pics",
                siteDescription = "Images",
            )
        val imageUniqueName = "1234567890abcdef1234567890abcdef"

        coEvery { commonService.fetchRandomImage() } returns ImageFileDTO(url = "https://cdn.example.com/random.png")
        coEvery { commonService.fetchImage(imageUniqueName) } returns ImageFileDTO(url = "https://cdn.example.com/image.png")

        testControllerApplication(
            routes = {
                route("api") {
                    siteInitRoute(commonService)
                }
                commonRoute(commonService)
            },
        ) { client ->
            assertRejected(client.postSiteInit(valid.copy(siteTitle = "")))
            assertRejected(client.postSiteInit(valid.copy(siteExternalUrl = "bad-url")))
            assertRejected(client.postSiteInit(valid.copy(username = "bad")))
            assertRejected(client.postSiteInit(valid.copy(password = "bad")))
            assertRejected(client.postSiteInit(valid.copy(email = "bad-email")))
            assertRejected(client.get("/s/bad"))

            assertContentEquals(byteArrayOf(42, 43, 44), client.get("/random?id=url").bodyAsBytes())
            assertContentEquals(byteArrayOf(42, 43, 44), client.get("/s/$imageUniqueName").bodyAsBytes())
        }
    }

    @Test
    fun `album validation rejects every invalid branch`() {
        val albumService = mockk<AlbumService>(relaxed = true)

        testControllerApplication(
            routes = {
                route("api") {
                    albumRoute(albumService)
                }
            },
        ) { client ->
            assertRejected(client.postJson("/api/album", AlbumSelfInsertRequest(name = "")))
            assertRejected(client.patchJson("/api/album/10", AlbumSelfPatchRequest()))
            assertRejected(client.patchJson("/api/album/10", AlbumSelfPatchRequest(name = "")))
            assertRejected(client.postJson("/api/album/manage", AlbumManageInsertRequest(userId = 1L, name = "")))
            assertRejected(client.patchJson("/api/album/manage/10", AlbumManagePatchRequest()))
            assertRejected(client.patchJson("/api/album/manage/10", AlbumManagePatchRequest(name = "")))
        }
    }

    @Test
    fun `group validation rejects every invalid branch`() {
        val groupService = mockk<GroupService>(relaxed = true)
        val config = ControllerTestFixtures.groupConfig()

        testControllerApplication(
            routes = {
                route("api") {
                    groupRoute(groupService)
                }
            },
        ) { client ->
            assertRejected(client.postJson("/api/group", GroupInsertRequest("", null, config, 1L, listOf("ROLE_USER"))))
            assertRejected(client.postJson("/api/group", GroupInsertRequest("group", null, config, 1L, emptyList())))
            assertRejected(client.patchJson("/api/group/2", GroupPatchRequest()))
            assertRejected(client.patchJson("/api/group/2", GroupPatchRequest(name = "")))
            assertRejected(client.patchJson("/api/group/2", GroupPatchRequest(roles = emptyList())))
            assertRejected(client.putJson("/api/group/2", GroupPutRequest("", null, 1L, config, listOf("ROLE_USER"))))
            assertRejected(client.putJson("/api/group/2", GroupPutRequest("group", null, 1L, config, emptyList())))
        }
    }

    @Test
    fun `strategy validation rejects every invalid branch`() {
        val strategyService = mockk<StrategyService>(relaxed = true)

        testControllerApplication(
            routes = {
                route("api") {
                    strategyRoute(strategyService)
                }
            },
        ) { client ->
            assertRejected(client.postJson("/api/strategy", StrategyInsertRequest("", validS3())))
            assertRejected(client.postJson("/api/strategy", StrategyInsertRequest("s3", validS3(bucketName = ""))))
            assertRejected(client.postJson("/api/strategy", StrategyInsertRequest("s3", validS3(region = ""))))
            assertRejected(client.postJson("/api/strategy", StrategyInsertRequest("s3", validS3(publicUrl = "bad-url"))))
            assertRejected(client.postJson("/api/strategy", StrategyInsertRequest("webdav", validWebDav(serverUrl = "bad-url"))))
            assertRejected(client.patchJson("/api/strategy/1", StrategyPatchRequest()))
            assertRejected(client.patchJson("/api/strategy/1", StrategyPatchRequest(name = "")))
            assertRejected(client.patchJson("/api/strategy/1", StrategyPatchRequest(config = validS3(endpoint = "bad-url"))))
            assertRejected(client.patchJson("/api/strategy/1", StrategyPatchRequest(config = validS3(bucketName = ""))))
            assertRejected(client.patchJson("/api/strategy/1", StrategyPatchRequest(config = validS3(region = ""))))
            assertRejected(client.patchJson("/api/strategy/1", StrategyPatchRequest(config = validS3(publicUrl = "bad-url"))))
            assertRejected(client.patchJson("/api/strategy/1", StrategyPatchRequest(config = validWebDav(serverUrl = "bad-url"))))
        }
    }

    @Test
    fun `setting validation rejects every invalid branch`() {
        val settingService = mockk<SettingService>(relaxed = true)

        testControllerApplication(
            routes = {
                route("api") {
                    settingRoute(settingService)
                }
            },
        ) { client ->
            assertRejected(client.get("/api/setting/unknown"))
            assertRejected(client.patchJson("/api/setting/site", SiteSettingPatchRequest()))
            assertRejected(client.patchJson("/api/setting/site", SiteSettingPatchRequest(siteTitle = "")))
            assertRejected(client.patchJson("/api/setting/site", SiteSettingPatchRequest(siteExternalUrl = "bad-url")))
            assertRejected(client.patchJson("/api/setting/system", SystemSettingPatchRequest()))
        }
    }

    @Test
    fun `role and pat validation rejects every invalid branch`() {
        val roleService = mockk<RoleService>(relaxed = true)
        val patService = mockk<PersonalAccessTokenService>(relaxed = true)

        testControllerApplication(
            routes = {
                route("api") {
                    roleRoute(roleService)
                    personalAccessTokenRoute(patService)
                }
            },
        ) { client ->
            assertRejected(client.postJson("/api/role", RoleInsertRequest("", "User", null, emptyList())))
            assertRejected(client.postJson("/api/role", RoleInsertRequest("ROLE_USER", "", null, emptyList())))
            assertRejected(client.patchJson("/api/role/ROLE_USER", RolePatchRequest()))
            assertRejected(client.patchJson("/api/role/ROLE_USER", RolePatchRequest(displayName = "User")))
            assertRejected(
                client.postJson(
                    "/api/personal-access-token",
                    PersonalAccessTokenInsertRequest("", null, LocalDateTime(2030, 1, 1, 0, 0), emptyList()),
                ),
            )
            assertRejected(
                client.postJson(
                    "/api/personal-access-token",
                    PersonalAccessTokenInsertRequest("token", null, LocalDateTime(2020, 1, 1, 0, 0), emptyList()),
                ),
            )
            assertRejected(client.patchJson("/api/personal-access-token/1", PersonalAccessTokenPatchRequest()))
            assertRejected(client.patchJson("/api/personal-access-token/1", PersonalAccessTokenPatchRequest(name = "")))
        }
    }

    @Test
    fun `user validation rejects every invalid branch`() {
        val userService = mockk<UserService>(relaxed = true)

        testControllerApplication(
            routes = {
                route("api") {
                    userRoute(userService)
                }
            },
        ) { client ->
            assertRejected(client.patchJson("/api/user/self", UserSelfPatchRequest()))
            assertRejected(client.patchJson("/api/user/self", UserSelfPatchRequest(password = "bad")))
            assertRejected(client.patchJson("/api/user/self", UserSelfPatchRequest(email = "bad-email")))
            assertRejected(client.postJson("/api/user/manage", validUserInsert(username = "bad")))
            assertRejected(client.postJson("/api/user/manage", validUserInsert(password = "bad")))
            assertRejected(client.postJson("/api/user/manage", validUserInsert(email = "bad-email")))
            assertRejected(client.delete("/api/user/manage/1"))
            assertRejected(client.patchJson("/api/user/manage/2", UserManagePatchRequest()))
            assertRejected(client.patchJson("/api/user/manage/2", UserManagePatchRequest(password = "bad")))
            assertRejected(client.patchJson("/api/user/manage/2", UserManagePatchRequest(email = "bad-email")))
            assertRejected(client.patch("/api/user/ban/1"))
            assertRejected(client.patch("/api/user/unban/1"))
        }
    }

    @Test
    fun `image validation and remote file branches are covered`() {
        val imageService = mockk<ImageService>(relaxed = true)

        coEvery { imageService.fetchSelfImageFile(1L, 9L) } returns ImageFileDTO(url = "https://cdn.example.com/file.png")
        coEvery { imageService.fetchSelfImageThumbnailFile(1L, 9L) } returns ImageFileDTO(url = "https://cdn.example.com/thumb.png")
        coEvery { imageService.fetchImageFile(9L) } returns ImageFileDTO(url = "https://cdn.example.com/manage-file.png")
        coEvery { imageService.fetchImageThumbnailFile(9L) } returns ImageFileDTO(url = "https://cdn.example.com/manage-thumb.png")

        testControllerApplication(
            routes = {
                route("api") {
                    imageRoute(imageService)
                }
            },
        ) { client ->
            assertRejected(client.post("/api/image") { setBody(twoImageFiles()) })
            assertRejected(client.post("/api/image") { setBody(textFile()) })
            assertRejected(client.post("/api/image") { setBody(noFile()) })
            assertRejected(client.patchJson("/api/image/9", ImagePatchRequest()))
            assertRejected(client.patchJson("/api/image/9", ImagePatchRequest(displayName = "")))
            assertRejected(client.patchJson("/api/image/manage/9", ImageManagePatchRequest()))
            assertRejected(client.patchJson("/api/image/manage/9", ImageManagePatchRequest(displayName = "")))

            assertContentEquals(byteArrayOf(42, 43, 44), client.get("/api/image/9/file").bodyAsBytes())
            assertContentEquals(byteArrayOf(42, 43, 44), client.get("/api/image/9/thumbnail").bodyAsBytes())
            assertContentEquals(byteArrayOf(42, 43, 44), client.get("/api/image/manage/9/file").bodyAsBytes())
            assertContentEquals(byteArrayOf(42, 43, 44), client.get("/api/image/manage/9/thumbnail").bodyAsBytes())
        }
    }

    private suspend fun assertRejected(response: HttpResponse) {
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.compactBody().contains("\"isSuccessful\":false"))
    }

    private suspend fun io.ktor.client.HttpClient.postSiteInit(request: SiteInitRequest): HttpResponse =
        postJson("/api/site/init", request)

    private suspend inline fun <reified T : Any> io.ktor.client.HttpClient.postJson(
        url: String,
        body: T,
    ): HttpResponse =
        post(url) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

    private suspend inline fun <reified T : Any> io.ktor.client.HttpClient.patchJson(
        url: String,
        body: T,
    ): HttpResponse =
        patch(url) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

    private suspend inline fun <reified T : Any> io.ktor.client.HttpClient.putJson(
        url: String,
        body: T,
    ): HttpResponse =
        put(url) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

    private fun validS3(
        endpoint: String = "https://s3.example.com",
        bucketName: String = "bucket",
        region: String = "us-east-1",
        publicUrl: String = "https://cdn.example.com",
    ): S3Strategy =
        S3Strategy(
            endpoint = endpoint,
            bucketName = bucketName,
            region = region,
            accessKey = "access",
            secretKey = "secret",
            uploadFolder = "/uploads",
            thumbnailFolder = "/thumbnails",
            publicUrl = publicUrl,
        )

    private fun validWebDav(serverUrl: String = "https://webdav.example.com"): WebDavStrategy =
        WebDavStrategy(
            serverUrl = serverUrl,
            username = "user",
            password = "password",
            uploadFolder = "/uploads",
            thumbnailFolder = "/thumbnails",
        )

    private fun validUserInsert(
        username: String = "tester2",
        password: String = "Passw0rd!",
        email: String = "tester2@example.com",
    ): UserManageInsertRequest =
        UserManageInsertRequest(
            groupId = 2L,
            username = username,
            password = password,
            email = email,
            isDefaultImagePrivate = true,
        )

    private fun twoImageFiles(): MultiPartFormDataContent =
        MultiPartFormDataContent(
            formData {
                append("file", byteArrayOf(1), fileHeaders("first.png", "image/png"))
                append("file", byteArrayOf(2), fileHeaders("second.png", "image/png"))
            },
        )

    private fun textFile(): MultiPartFormDataContent =
        MultiPartFormDataContent(
            formData {
                append("file", byteArrayOf(1), fileHeaders("note.txt", "text/plain"))
            },
        )

    private fun noFile(): MultiPartFormDataContent =
        MultiPartFormDataContent(
            formData {
                append("field", "value")
            },
        )

    private fun fileHeaders(
        filename: String,
        contentType: String,
    ): Headers =
        Headers.build {
            append(HttpHeaders.ContentType, contentType)
            append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"$filename\"")
        }
}
