package io.sakurasou.service.setting

import io.mockk.*
import io.sakurasou.constant.SETTING_SITE
import io.sakurasou.constant.SETTING_STRATEGY
import io.sakurasou.constant.SETTING_SYSTEM
import io.sakurasou.controller.request.StrategySettingPatchRequest
import io.sakurasou.model.DatabaseSingleton
import io.sakurasou.model.dao.setting.SettingDao
import io.sakurasou.model.dto.SettingUpdateDTO
import io.sakurasou.model.entity.Setting
import io.sakurasou.model.setting.SiteSetting
import io.sakurasou.model.setting.StrategySetting
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
    fun `update strategy setting should be successful`() = runBlocking {
        val request = StrategySettingPatchRequest(allowedImageTypes = listOf("jpg", "png"))
        val now = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val expectedDTO = SettingUpdateDTO(
            name = SETTING_STRATEGY,
            config = StrategySetting(allowedImageTypes = listOf("jpg", "png")),
            updateTime = now
        )

        coEvery { DatabaseSingleton.dbQuery<Int>(any()) } coAnswers {
            this.arg<suspend () -> Int>(0).invoke()
        }
        every { settingDao.updateSettingByName(expectedDTO) } returns 1

        settingService.updateStrategySetting(request)

        verify(exactly = 1) { settingDao.updateSettingByName(expectedDTO) }
    }

    @Test
    fun `getting system setting, should return correct setting`() = runBlocking {
        val expectedSetting = SystemSetting(defaultGroupId = 1, allowSignup = false)

        coEvery { DatabaseSingleton.dbQuery<SystemSetting>(any()) } coAnswers {
            this.arg<suspend () -> SystemSetting>(0).invoke()
        }
        every { settingDao.getSettingByName(SETTING_SYSTEM) } returns
                Setting(
                    name = SETTING_SYSTEM,
                    config = SystemSetting(defaultGroupId = 1, allowSignup = false),
                    createTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()),
                    updateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                )

        val systemSetting = settingService.getSystemSetting()

        assertEquals(expectedSetting, systemSetting)
    }

    @Test
    fun `getting site setting, should return correct setting`() = runBlocking {
        val expectedSetting = SiteSetting(
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

    @Test
    fun `getting strategy setting, should return correct setting`() = runBlocking {
        val expectedSetting = StrategySetting(allowedImageTypes = emptyList())

        coEvery { DatabaseSingleton.dbQuery<StrategySetting>(any()) } coAnswers {
            this.arg<suspend () -> StrategySetting>(0).invoke()
        }
        every { settingDao.getSettingByName(SETTING_STRATEGY) } returns
                Setting(
                    name = SETTING_STRATEGY,
                    config = StrategySetting(allowedImageTypes = emptyList()),
                    createTime = instant.toLocalDateTime(TimeZone.currentSystemDefault()),
                    updateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                )

        val strategySetting = settingService.getStrategySetting()

        assertEquals(expectedSetting, strategySetting)
    }
}