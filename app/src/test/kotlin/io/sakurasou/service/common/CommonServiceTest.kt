package io.sakurasou.service.common

import at.favre.lib.crypto.bcrypt.BCrypt
import io.mockk.*
import io.sakurasou.controller.request.SiteInitRequest
import io.sakurasou.exception.controller.status.SiteRepeatedInitializationException
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.model.dao.album.AlbumDao
import io.sakurasou.model.dao.image.ImageDao
import io.sakurasou.model.dao.strategy.StrategyDao
import io.sakurasou.model.dao.user.UserDao
import io.sakurasou.model.dto.UserInsertDTO
import io.sakurasou.model.setting.SiteSetting
import io.sakurasou.model.setting.SystemStatus
import io.sakurasou.service.setting.SettingService
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
    fun `initSite should throw SiteRepeatedInitializationException if already initialized`(): Unit = runBlocking {
        val siteInitRequest = SiteInitRequest(
            username = "testUser",
            password = "testPassword",
            email = "test@example.com",
            siteTitle = "Test Site",
            siteSubtitle = "Test Subtitle",
            siteDescription = "Test Description",
            siteExternalUrl = "http://localhost:8080"
        )
        coEvery { settingService.getSystemStatus() } returns SystemStatus(isInit = true)

        assertFailsWith<SiteRepeatedInitializationException> {
            commonService.initSite(siteInitRequest)
        }
    }

    @Test
    fun `initSite should initialize site correctly`() = runBlocking {
        val siteInitRequest = SiteInitRequest(
            username = "testUser",
            password = "testPassword",
            email = "test@example.com",
            siteTitle = "Test Site",
            siteSubtitle = "Test Subtitle",
            siteDescription = "Test Description",
            siteExternalUrl = "http://localhost:8080"
        )
        val instant = Clock.System.now()
        val now = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val encodedPassword = "encodedPassword"
        val userInsertDTO = UserInsertDTO(
            groupId = 1,
            username = "testUser",
            password = encodedPassword,
            email = "test@example.com",
            isDefaultImagePrivate = true,
            defaultAlbumId = null,
            isBanned = false,
            createTime = now,
            updateTime = now
        )
        val oldSiteSetting = SiteSetting(
            siteExternalUrl = "http://localhost:8080",
            siteTitle = "oldTitle",
            siteSubtitle = "oldSubtitle",
            siteKeyword = "oldDescription",
            siteDescription = "oldKeyword",
            homePageRandomPicDisplay = true
        )
        val siteSettingConfig = SiteSetting(
            siteExternalUrl = siteInitRequest.siteExternalUrl,
            siteTitle = siteInitRequest.siteTitle,
            siteSubtitle = siteInitRequest.siteSubtitle,
            siteDescription = siteInitRequest.siteDescription,
            siteKeyword = oldSiteSetting.siteKeyword,
            homePageRandomPicDisplay = oldSiteSetting.homePageRandomPicDisplay
        )
        val systemStatus = SystemStatus(isInit = true)

        every { Clock.System.now() } returns instant
        coEvery { settingService.getSystemStatus() } returns SystemStatus(isInit = false)
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