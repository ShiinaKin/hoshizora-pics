package io.sakurasou.service.common

import at.favre.lib.crypto.bcrypt.BCrypt
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import io.sakurasou.hoshizora.controller.request.SiteInitRequest
import io.sakurasou.hoshizora.exception.controller.status.SiteRepeatedInitializationException
import io.sakurasou.hoshizora.model.DatabaseSingleton
import io.sakurasou.hoshizora.model.dao.album.AlbumDao
import io.sakurasou.hoshizora.model.dao.image.ImageDao
import io.sakurasou.hoshizora.model.dao.strategy.StrategyDao
import io.sakurasou.hoshizora.model.dao.user.UserDao
import io.sakurasou.hoshizora.model.dto.UserInsertDTO
import io.sakurasou.hoshizora.model.setting.SiteSetting
import io.sakurasou.hoshizora.model.setting.SystemStatus
import io.sakurasou.hoshizora.service.common.CommonServiceImpl
import io.sakurasou.hoshizora.service.setting.SettingService
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

/**
 * @author Shiina Kin
 * 2024/9/13 19:47
 */
class CommonServiceTest {
    private lateinit var userDao: UserDao
    private lateinit var albumDao: AlbumDao
    private lateinit var imageDao: ImageDao
    private lateinit var strategyDao: StrategyDao
    private lateinit var settingService: SettingService
    private lateinit var commonService: CommonServiceImpl

    @BeforeTest
    fun setUp() {
        mockkObject(DatabaseSingleton)
        mockkStatic(BCrypt::class)
        mockkObject(Clock.System)
        userDao = mockk()
        albumDao = mockk()
        imageDao = mockk()
        strategyDao = mockk()
        settingService = mockk()
        commonService = CommonServiceImpl(userDao, albumDao, strategyDao, imageDao, settingService)
        coEvery { DatabaseSingleton.dbQuery<Unit>(any()) } coAnswers {
            this.arg<suspend () -> Unit>(0).invoke()
        }
    }

    @Test
    fun `initSite should throw SiteRepeatedInitializationException if already initialized`(): Unit =
        runBlocking {
            val siteInitRequest =
                SiteInitRequest(
                    username = "testUser",
                    password = "testPassword",
                    email = "test@example.com",
                    siteTitle = "Test Site",
                    siteSubtitle = "Test Subtitle",
                    siteDescription = "Test Description",
                    siteExternalUrl = "http://localhost:8080",
                )
            coEvery { settingService.getSystemStatus() } returns SystemStatus(isInit = true, version = "1.0.0")

            assertFailsWith<SiteRepeatedInitializationException> {
                commonService.initSite(siteInitRequest)
            }
        }

    @Test
    fun `initSite should initialize site correctly`() =
        runBlocking {
            val siteInitRequest =
                SiteInitRequest(
                    username = "testUser",
                    password = "testPassword",
                    email = "test@example.com",
                    siteTitle = "Test Site",
                    siteSubtitle = "Test Subtitle",
                    siteDescription = "Test Description",
                    siteExternalUrl = "http://localhost:8080",
                )
            val instant = Clock.System.now()
            val now = instant.toLocalDateTime(TimeZone.UTC)
            val encodedPassword = "encodedPassword"
            val userInsertDTO =
                UserInsertDTO(
                    groupId = 1,
                    username = "testUser",
                    password = encodedPassword,
                    email = "test@example.com",
                    isDefaultImagePrivate = true,
                    defaultAlbumId = null,
                    isBanned = false,
                    createTime = now,
                    updateTime = now,
                )
            val oldSiteSetting =
                SiteSetting(
                    siteExternalUrl = "http://localhost:8080",
                    siteTitle = "oldTitle",
                    siteSubtitle = "oldSubtitle",
                    siteDescription = "oldKeyword",
                )
            val siteSettingConfig =
                SiteSetting(
                    siteExternalUrl = siteInitRequest.siteExternalUrl,
                    siteTitle = siteInitRequest.siteTitle,
                    siteSubtitle = siteInitRequest.siteSubtitle,
                    siteDescription = siteInitRequest.siteDescription,
                )
            val systemStatus = SystemStatus(isInit = true, version = "1.0.0")

            every { Clock.System.now() } returns instant
            coEvery { settingService.getSystemStatus() } returns SystemStatus(isInit = false, version = "1.0.0")
            coEvery { settingService.getSiteSetting() } returns oldSiteSetting
            every { BCrypt.withDefaults().hashToString(12, siteInitRequest.password.toCharArray()) } returns encodedPassword
            every { userDao.saveUser(userInsertDTO) } returns 1
            every { albumDao.initAlbumForUser(1) } returns 1
            every { userDao.updateUserDefaultAlbumId(1L, 1L) } returns 1
            coEvery { settingService.updateSiteSetting(siteSettingConfig) } just Runs
            coEvery { settingService.updateSystemStatus(systemStatus) } just Runs

            commonService.initSite(siteInitRequest)

            coVerify(exactly = 1) { DatabaseSingleton.dbQuery<Unit>(any()) }
            verify(exactly = 1) { userDao.saveUser(userInsertDTO) }
            verify(exactly = 1) { albumDao.initAlbumForUser(1) }
            verify(exactly = 1) { userDao.updateUserDefaultAlbumId(1L, 1L) }
            coVerify(exactly = 1) { settingService.updateSiteSetting(siteSettingConfig) }
            coVerify(exactly = 1) { settingService.updateSystemStatus(systemStatus) }
        }
}
