package io.sakurasou.service.setting

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.sakurasou.constant.SETTING_SITE
import io.sakurasou.constant.SETTING_SYSTEM
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.model.dao.setting.SettingDao
import io.sakurasou.model.entity.Setting
import io.sakurasou.model.setting.SiteSetting
import io.sakurasou.model.setting.SystemSetting
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @author Shiina Kin
 * 2024/9/13 21:46
 */
class SettingServiceTest {
    private lateinit var settingDao: SettingDao
    private lateinit var settingService: SettingService
    private lateinit var instant: Instant

    @BeforeTest
    fun setUp() {
        mockkObject(DatabaseSingleton)
        mockkObject(Clock.System)
        settingDao = mockk()
        settingService = SettingServiceImpl(settingDao)
        instant = Clock.System.now()
        every { Clock.System.now() } returns instant
    }

    @Test
    fun `getting system setting, should return correct setting`() = runBlocking {
        val expectedSetting = SystemSetting(
            defaultGroupId = 1,
            allowSignup = false,
            allowRandomFetch = false
        )

        coEvery { DatabaseSingleton.dbQuery<SystemSetting>(any()) } coAnswers {
            this.arg<suspend () -> SystemSetting>(0).invoke()
        }
        every { settingDao.getSettingByName(SETTING_SYSTEM) } returns
                Setting(
                    name = SETTING_SYSTEM,
                    config = SystemSetting(defaultGroupId = 1, allowSignup = false, allowRandomFetch = false),
                    createTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()),
                    updateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                )

        val systemSetting = settingService.getSystemSetting()

        assertEquals(expectedSetting, systemSetting)
    }

    @Test
    fun `getting site setting, should return correct setting`() = runBlocking {
        val expectedSetting = SiteSetting(
            siteExternalUrl = "http://localhost:8080",
            siteTitle = "Title",
            siteSubtitle = "Subtitle",
            siteDescription = "Description",
            siteKeyword = "Keyword",
            homePageRandomPicDisplay = false
        )

        coEvery { DatabaseSingleton.dbQuery<SiteSetting>(any()) } coAnswers {
            this.arg<suspend () -> SiteSetting>(0).invoke()
        }
        every { settingDao.getSettingByName(SETTING_SITE) } returns
                Setting(
                    name = SETTING_SITE,
                    config = SiteSetting(
                        siteExternalUrl = "http://localhost:8080",
                        siteTitle = "Title",
                        siteSubtitle = "Subtitle",
                        siteDescription = "Description",
                        siteKeyword = "Keyword",
                        homePageRandomPicDisplay = false
                    ),
                    createTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()),
                    updateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                )

        val siteSetting = settingService.getSiteSetting()

        assertEquals(expectedSetting, siteSetting)
    }
}