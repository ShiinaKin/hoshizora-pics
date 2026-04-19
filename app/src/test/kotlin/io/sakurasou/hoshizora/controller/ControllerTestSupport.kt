package io.sakurasou.hoshizora.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.sakurasou.hoshizora.config.JwtConfig
import io.sakurasou.hoshizora.config.exceptionHandler
import io.sakurasou.hoshizora.constant.ALBUM_DELETE_ALL
import io.sakurasou.hoshizora.constant.ALBUM_DELETE_SELF
import io.sakurasou.hoshizora.constant.ALBUM_READ_ALL_ALL
import io.sakurasou.hoshizora.constant.ALBUM_READ_ALL_SINGLE
import io.sakurasou.hoshizora.constant.ALBUM_READ_SELF_ALL
import io.sakurasou.hoshizora.constant.ALBUM_READ_SELF_SINGLE
import io.sakurasou.hoshizora.constant.ALBUM_WRITE_ALL
import io.sakurasou.hoshizora.constant.ALBUM_WRITE_SELF
import io.sakurasou.hoshizora.constant.GROUP_DELETE
import io.sakurasou.hoshizora.constant.GROUP_READ_ALL
import io.sakurasou.hoshizora.constant.GROUP_READ_SINGLE
import io.sakurasou.hoshizora.constant.GROUP_WRITE
import io.sakurasou.hoshizora.constant.IMAGE_DELETE_ALL
import io.sakurasou.hoshizora.constant.IMAGE_DELETE_SELF
import io.sakurasou.hoshizora.constant.IMAGE_READ_ALL_ALL
import io.sakurasou.hoshizora.constant.IMAGE_READ_ALL_SINGLE
import io.sakurasou.hoshizora.constant.IMAGE_READ_SELF_ALL
import io.sakurasou.hoshizora.constant.IMAGE_READ_SELF_SINGLE
import io.sakurasou.hoshizora.constant.IMAGE_WRITE_ALL
import io.sakurasou.hoshizora.constant.IMAGE_WRITE_SELF
import io.sakurasou.hoshizora.constant.PERMISSION_READ
import io.sakurasou.hoshizora.constant.PERSONAL_ACCESS_TOKEN_READ_SELF
import io.sakurasou.hoshizora.constant.PERSONAL_ACCESS_TOKEN_WRITE_SELF
import io.sakurasou.hoshizora.constant.ROLE_READ_ALL
import io.sakurasou.hoshizora.constant.ROLE_READ_SELF
import io.sakurasou.hoshizora.constant.ROLE_WRITE_ALL
import io.sakurasou.hoshizora.constant.SETTING_READ
import io.sakurasou.hoshizora.constant.SETTING_WRITE
import io.sakurasou.hoshizora.constant.STRATEGY_DELETE
import io.sakurasou.hoshizora.constant.STRATEGY_READ_ALL
import io.sakurasou.hoshizora.constant.STRATEGY_READ_SINGLE
import io.sakurasou.hoshizora.constant.STRATEGY_WRITE
import io.sakurasou.hoshizora.constant.USER_BAN
import io.sakurasou.hoshizora.constant.USER_DELETE
import io.sakurasou.hoshizora.constant.USER_READ_ALL_ALL
import io.sakurasou.hoshizora.constant.USER_READ_ALL_SINGLE
import io.sakurasou.hoshizora.constant.USER_READ_SELF
import io.sakurasou.hoshizora.constant.USER_WRITE_ALL
import io.sakurasou.hoshizora.constant.USER_WRITE_SELF
import io.sakurasou.hoshizora.controller.vo.AlbumManagePageVO
import io.sakurasou.hoshizora.controller.vo.AlbumManageVO
import io.sakurasou.hoshizora.controller.vo.AlbumPageVO
import io.sakurasou.hoshizora.controller.vo.AlbumVO
import io.sakurasou.hoshizora.controller.vo.CommonSiteSetting
import io.sakurasou.hoshizora.controller.vo.GroupPageVO
import io.sakurasou.hoshizora.controller.vo.GroupVO
import io.sakurasou.hoshizora.controller.vo.HoshizoraStatusVO
import io.sakurasou.hoshizora.controller.vo.ImageManagePageVO
import io.sakurasou.hoshizora.controller.vo.ImageManageVO
import io.sakurasou.hoshizora.controller.vo.ImagePageVO
import io.sakurasou.hoshizora.controller.vo.ImageVO
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.PermissionVO
import io.sakurasou.hoshizora.controller.vo.PersonalAccessTokenPageVO
import io.sakurasou.hoshizora.controller.vo.RolePageVO
import io.sakurasou.hoshizora.controller.vo.RoleVO
import io.sakurasou.hoshizora.controller.vo.StrategyPageVO
import io.sakurasou.hoshizora.controller.vo.StrategyVO
import io.sakurasou.hoshizora.controller.vo.SystemOverviewVO
import io.sakurasou.hoshizora.controller.vo.SystemStatisticsVO
import io.sakurasou.hoshizora.controller.vo.SystemStatusVO
import io.sakurasou.hoshizora.controller.vo.UserPageVO
import io.sakurasou.hoshizora.controller.vo.UserVO
import io.sakurasou.hoshizora.di.DI
import io.sakurasou.hoshizora.di.DIManager
import io.sakurasou.hoshizora.extension.Principal
import io.sakurasou.hoshizora.model.group.GroupConfig
import io.sakurasou.hoshizora.model.group.GroupStrategyConfig
import io.sakurasou.hoshizora.model.group.ImageType
import io.sakurasou.hoshizora.model.DatabaseSingleton
import io.sakurasou.hoshizora.model.dao.relation.RelationDao
import io.sakurasou.hoshizora.model.strategy.LocalStrategy
import io.sakurasou.hoshizora.model.strategy.StrategyType
import io.sakurasou.hoshizora.plugins.Cache
import io.sakurasou.hoshizora.plugins.memoryProvider
import kotlinx.datetime.LocalDateTime
import java.time.Instant
import java.util.Date
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun testControllerApplication(
    principal: Principal = Principal(id = 1L, groupId = 2L, username = "tester"),
    externalClient: HttpClient = mockExternalHttpClient(),
    routes: Route.() -> Unit,
    testBlock: suspend ApplicationTestBuilder.(HttpClient) -> Unit,
) {
    JwtConfig.init(
        jwtSecret = "controller-test-secret",
        jwtIssuer = "controller-test-issuer",
        jwtAudience = "controller-test-audience",
        jwtRealm = "controller-test-realm",
    )
    installAuthorizationMocks(externalClient)

    try {
        testApplication {
            application {
                installControllerTestPlugins()
                routing {
                    authenticate("auth-jwt") {
                        routes()
                    }
                }
            }

            val jsonClient =
                createClient {
                    install(ClientContentNegotiation) {
                        json()
                    }
                    defaultRequest {
                        bearerAuth(testJwt(principal))
                    }
                }

            testBlock(jsonClient)
        }
    } finally {
        unmockkObject(DatabaseSingleton)
        unmockkObject(DIManager)
    }
}

internal suspend fun HttpResponse.compactBody(): String = bodyAsText().filterNot { it.isWhitespace() }

private fun Application.installControllerTestPlugins() {
    install(ServerContentNegotiation) {
        json()
    }
    authentication {
        jwt("auth-jwt") {
            realm = JwtConfig.realm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(JwtConfig.secret))
                    .withAudience(JwtConfig.audience)
                    .withIssuer(JwtConfig.issuer)
                    .build(),
            )
            validate { credential ->
                if (credential.payload.audience.contains(JwtConfig.audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
    install(StatusPages) {
        exceptionHandler()
    }
    install(Cache) {
        memoryProvider(1.minutes)
    }
    install(RateLimit) {
        register(RateLimitName("randomFetchLimit")) {
            rateLimiter(limit = 1000, refillPeriod = 1.seconds)
        }
        register(RateLimitName("anonymousGetLimit")) {
            rateLimiter(limit = 1000, refillPeriod = 1.seconds)
        }
    }
}

private fun installAuthorizationMocks(externalClient: HttpClient) {
    val relationDao =
        mockk<RelationDao> {
            every { listPermissionByRole("ROLE_CONTROLLER_TEST") } returns allControllerPermissions
        }
    val di =
        mockk<DI> {
            every { get(RelationDao::class) } returns relationDao
            every { get(HttpClient::class) } returns externalClient
        }

    mockkObject(DIManager)
    every { DIManager.getDIInstance() } returns di

    mockkObject(DatabaseSingleton)
    coEvery { DatabaseSingleton.dbQuery<List<String>>(any()) } coAnswers {
        arg<suspend () -> List<String>>(0).invoke()
    }
}

private fun mockExternalHttpClient(bytes: ByteArray = byteArrayOf(42, 43, 44)): HttpClient =
    HttpClient(MockEngine) {
        engine {
            addHandler {
                respond(
                    content = bytes,
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "image/png"),
                )
            }
        }
    }

private fun testJwt(principal: Principal): String =
    JWT
        .create()
        .withAudience(JwtConfig.audience)
        .withIssuer(JwtConfig.issuer)
        .withClaim("id", principal.id)
        .withClaim("username", principal.username)
        .withClaim("groupId", principal.groupId)
        .withClaim("roles", listOf("ROLE_CONTROLLER_TEST"))
        .withExpiresAt(Date.from(Instant.now().plusSeconds(3600)))
        .sign(Algorithm.HMAC256(JwtConfig.secret))

private val allControllerPermissions =
    listOf(
        USER_READ_SELF,
        USER_READ_ALL_SINGLE,
        USER_READ_ALL_ALL,
        USER_WRITE_SELF,
        USER_WRITE_ALL,
        USER_DELETE,
        USER_BAN,
        GROUP_READ_SINGLE,
        GROUP_READ_ALL,
        GROUP_WRITE,
        GROUP_DELETE,
        ROLE_READ_SELF,
        ROLE_READ_ALL,
        ROLE_WRITE_ALL,
        PERMISSION_READ,
        SETTING_READ,
        SETTING_WRITE,
        STRATEGY_READ_ALL,
        STRATEGY_READ_SINGLE,
        STRATEGY_WRITE,
        STRATEGY_DELETE,
        IMAGE_READ_SELF_SINGLE,
        IMAGE_READ_SELF_ALL,
        IMAGE_READ_ALL_SINGLE,
        IMAGE_READ_ALL_ALL,
        IMAGE_WRITE_SELF,
        IMAGE_WRITE_ALL,
        IMAGE_DELETE_SELF,
        IMAGE_DELETE_ALL,
        ALBUM_READ_SELF_SINGLE,
        ALBUM_READ_SELF_ALL,
        ALBUM_READ_ALL_SINGLE,
        ALBUM_READ_ALL_ALL,
        ALBUM_WRITE_SELF,
        ALBUM_WRITE_ALL,
        ALBUM_DELETE_SELF,
        ALBUM_DELETE_ALL,
        PERSONAL_ACCESS_TOKEN_READ_SELF,
        PERSONAL_ACCESS_TOKEN_WRITE_SELF,
    )

internal object ControllerTestFixtures {
    val now: LocalDateTime = LocalDateTime(2026, 1, 2, 3, 4, 5)
    val principal: Principal = Principal(id = 1L, groupId = 2L, username = "tester")

    fun groupConfig(): GroupConfig =
        GroupConfig(
            groupStrategyConfig =
                GroupStrategyConfig(
                    allowedImageTypes = setOf(ImageType.JPG, ImageType.PNG),
                ),
        )

    fun commonSiteSetting(): CommonSiteSetting =
        CommonSiteSetting(
            isSiteInit = true,
            siteTitle = "Hoshizora",
            siteSubTitle = "Pics",
            siteDescription = "Images",
            siteAllowSignup = true,
        )

    fun albumVO(): AlbumVO =
        AlbumVO(
            id = 10L,
            name = "album",
            description = "desc",
            imageCount = 3L,
            isUncategorized = false,
            isDefault = false,
            createTime = now,
        )

    fun albumManageVO(): AlbumManageVO =
        AlbumManageVO(
            id = 10L,
            name = "album",
            userId = 1L,
            username = "tester",
            description = "desc",
            imageCount = 3L,
            isUncategorized = false,
            isDefault = false,
            createTime = now,
        )

    fun albumPageResult(): PageResult<AlbumPageVO> =
        PageResult(
            page = 1L,
            pageSize = 20,
            total = 1L,
            totalPage = 1L,
            data =
                listOf(
                    AlbumPageVO(
                        id = 10L,
                        name = "album",
                        imageCount = 3L,
                        isUncategorized = false,
                        isDefault = false,
                        createTime = now,
                    ),
                ),
        )

    fun albumManagePageResult(): PageResult<AlbumManagePageVO> =
        PageResult(
            page = 1L,
            pageSize = 20,
            total = 1L,
            totalPage = 1L,
            data =
                listOf(
                    AlbumManagePageVO(
                        id = 10L,
                        name = "album",
                        userId = 1L,
                        username = "tester",
                        userEmail = "tester@example.com",
                        imageCount = 3L,
                        createTime = now,
                    ),
                ),
        )

    fun groupVO(): GroupVO =
        GroupVO(
            id = 2L,
            name = "group",
            description = "desc",
            groupConfig = groupConfig(),
            strategyId = 1L,
            strategyName = "local",
            roles = listOf("ROLE_USER"),
            isSystemReserved = false,
            createTime = now,
        )

    fun groupPageResult(): PageResult<GroupPageVO> =
        PageResult(
            page = 1L,
            pageSize = 20,
            total = 1L,
            totalPage = 1L,
            data =
                listOf(
                    GroupPageVO(
                        id = 2L,
                        name = "group",
                        strategyId = 1L,
                        strategyName = "local",
                        userCount = 1L,
                        imageCount = 2L,
                        imageSize = 10.0,
                        isSystemReserved = false,
                        createTime = now,
                    ),
                ),
        )

    fun strategyVO(): StrategyVO =
        StrategyVO(
            id = 1L,
            name = "local",
            config = LocalStrategy("/uploads", "/thumbnails"),
            type = StrategyType.LOCAL,
            createTime = now,
        )

    fun strategyPageResult(): PageResult<StrategyPageVO> =
        PageResult(
            page = 1L,
            pageSize = 20,
            total = 1L,
            totalPage = 1L,
            data =
                listOf(
                    StrategyPageVO(
                        id = 1L,
                        name = "local",
                        isSystemReserved = false,
                        type = StrategyType.LOCAL,
                        createTime = now,
                    ),
                ),
        )

    fun userVO(): UserVO =
        UserVO(
            id = 1L,
            username = "tester",
            groupId = 2L,
            groupName = "group",
            email = "tester@example.com",
            isDefaultImagePrivate = true,
            isBanned = false,
            createTime = now,
            imageCount = 2L,
            albumCount = 1L,
            totalImageSize = 12.0,
            allSize = 1024.0,
        )

    fun userPageResult(): PageResult<UserPageVO> =
        PageResult(
            page = 1L,
            pageSize = 20,
            total = 1L,
            totalPage = 1L,
            data =
                listOf(
                    UserPageVO(
                        id = 1L,
                        username = "tester",
                        groupName = "group",
                        isBanned = false,
                        createTime = now,
                        imageCount = 2L,
                        albumCount = 1L,
                        totalImageSize = 12.0,
                    ),
                ),
        )

    fun roleVO(): RoleVO =
        RoleVO(
            name = "ROLE_USER",
            displayName = "User",
            description = "role",
            permissions = listOf(PermissionVO("user:read:self", "read self")),
        )

    fun rolePageResult(): PageResult<RolePageVO> =
        PageResult(
            page = 1L,
            pageSize = 20,
            total = 1L,
            totalPage = 1L,
            data = listOf(RolePageVO("ROLE_USER", "User", "role")),
        )

    fun patPageResult(): PageResult<PersonalAccessTokenPageVO> =
        PageResult(
            page = 1L,
            pageSize = 20,
            total = 1L,
            totalPage = 1L,
            data =
                listOf(
                    PersonalAccessTokenPageVO(
                        id = 5L,
                        name = "token",
                        description = "desc",
                        createTime = now,
                        expireTime = LocalDateTime(2030, 1, 1, 0, 0),
                        isExpired = false,
                    ),
                ),
        )

    fun systemStatistics(): SystemStatisticsVO =
        SystemStatisticsVO(
            totalImageCount = 2L,
            totalAlbumCount = 1L,
            totalUserCount = 1L,
            totalUsedSpace = 12.0,
        )

    fun systemOverview(): SystemOverviewVO =
        SystemOverviewVO(
            hoshizoraStatus =
                HoshizoraStatusVO(
                    version = "1.0.0",
                    buildTime = "2026-01-01T00:00:00",
                    commitId = "abcdef123456",
                ),
            systemStatus =
                SystemStatusVO(
                    javaVersion = "21",
                    databaseVersion = "PostgreSQL",
                    operatingSystem = "macOS",
                    serverTimeZone = "Asia/Shanghai",
                    serverLanguage = "zh",
                ),
        )

    fun imageVO(): ImageVO =
        ImageVO(
            id = 9L,
            ownerId = 1L,
            ownerName = "tester",
            displayName = "image",
            albumId = 10L,
            albumName = "album",
            originName = "image.png",
            description = "desc",
            mimeType = "image/png",
            size = 12.0,
            width = 100,
            height = 100,
            md5 = "md5",
            sha256 = "sha256",
            isPrivate = false,
            isAllowedRandomFetch = true,
            createTime = now,
        )

    fun imageManageVO(): ImageManageVO =
        ImageManageVO(
            id = 9L,
            ownerId = 1L,
            ownerName = "tester",
            groupId = 2L,
            groupName = "group",
            strategyId = 1L,
            strategyName = "local",
            strategyType = "LOCAL",
            displayName = "image",
            albumId = 10L,
            albumName = "album",
            originName = "image.png",
            description = "desc",
            mimeType = "image/png",
            size = 12.0,
            width = 100,
            height = 100,
            md5 = "md5",
            sha256 = "sha256",
            isPrivate = false,
            isAllowedRandomFetch = true,
            createTime = now,
        )

    fun imagePageResult(): PageResult<ImagePageVO> =
        PageResult(
            page = 1L,
            pageSize = 20,
            total = 1L,
            totalPage = 1L,
            data = listOf(ImagePageVO(9L, "image", false, "abc", now)),
        )

    fun imageManagePageResult(): PageResult<ImageManagePageVO> =
        PageResult(
            page = 1L,
            pageSize = 20,
            total = 1L,
            totalPage = 1L,
            data = listOf(ImageManagePageVO(9L, "image", 1L, "tester", "tester@example.com", false, "abc", now)),
        )
}
