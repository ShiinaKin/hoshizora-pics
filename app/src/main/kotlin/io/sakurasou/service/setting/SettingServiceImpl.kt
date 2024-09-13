package io.sakurasou.service.setting

import io.sakurasou.constant.SETTING_SITE
import io.sakurasou.constant.SETTING_STRATEGY
import io.sakurasou.constant.SETTING_SYSTEM
import io.sakurasou.controller.request.SiteSettingPatchRequest
import io.sakurasou.controller.request.StrategySettingPatchRequest
import io.sakurasou.controller.request.SystemSettingPatchRequest
import io.sakurasou.exception.ConfigTypeNotMatchException
import io.sakurasou.exception.MissingNecessaryColumnException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.dao.setting.SettingDao
import io.sakurasou.model.dto.SettingUpdateDTO
import io.sakurasou.model.setting.SiteSetting
import io.sakurasou.model.setting.StrategySetting
import io.sakurasou.model.setting.SystemSetting
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/13 15:00
 */
class SettingServiceImpl(
    private val settingDao: SettingDao
) : SettingService {
    override suspend fun updateSystemSetting(systemSetting: SystemSettingPatchRequest) {
        val systemSettingConfig = SystemSetting(
            defaultGroupId = systemSetting.defaultGroupId,
            allowSignup = systemSetting.allowSignup
        )
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val settingUpdateDTO = SettingUpdateDTO(
            name = SETTING_SYSTEM,
            config = systemSettingConfig,
            updateTime = now
        )
        dbQuery {
            settingDao.updateSettingByName(settingUpdateDTO)
        }
    }

    override suspend fun updateSiteSetting(siteSetting: SiteSettingPatchRequest) {
        val siteSettingConfig = SiteSetting(
            siteTitle = siteSetting.siteTitle,
            siteSubtitle = siteSetting.siteSubtitle,
            siteDescription = siteSetting.siteDescription,
            siteKeyword = siteSetting.siteKeyword,
            homePageRandomPicDisplay = siteSetting.homePageRandomPicDisplay
        )
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val settingUpdateDTO = SettingUpdateDTO(
            name = SETTING_SITE,
            config = siteSettingConfig,
            updateTime = now
        )
        dbQuery {
            settingDao.updateSettingByName(settingUpdateDTO)
        }
    }

    override suspend fun updateStrategySetting(strategySetting: StrategySettingPatchRequest) {
        val strategySettingConfig = StrategySetting(
            allowedImageTypes = strategySetting.allowedImageTypes
        )
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val settingUpdateDTO = SettingUpdateDTO(
            name = SETTING_STRATEGY,
            config = strategySettingConfig,
            updateTime = now
        )
        dbQuery {
            settingDao.updateSettingByName(settingUpdateDTO)
        }
    }

    override suspend fun getSystemSetting(): SystemSetting {
        return dbQuery {
            settingDao.getSettingByName(SETTING_SYSTEM)
        }?.let {
            if (it.config !is SystemSetting) throw ConfigTypeNotMatchException()
            it.config
        } ?: run { throw MissingNecessaryColumnException() }
    }

    override suspend fun getSiteSetting(): SiteSetting {
        return dbQuery {
            settingDao.getSettingByName(SETTING_SITE)
        }?.let {
            if (it.config !is SiteSetting) throw ConfigTypeNotMatchException()
            it.config
        } ?: run { throw MissingNecessaryColumnException() }
    }

    override suspend fun getStrategySetting(): StrategySetting {
        return dbQuery {
            settingDao.getSettingByName(SETTING_STRATEGY)
        }?.let {
            if (it.config !is StrategySetting) throw ConfigTypeNotMatchException()
            it.config
        } ?: run { throw MissingNecessaryColumnException() }
    }
}