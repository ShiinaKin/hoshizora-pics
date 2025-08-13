package io.sakurasou.hoshizora.service.setting

import io.sakurasou.hoshizora.constant.SETTING_SITE
import io.sakurasou.hoshizora.constant.SETTING_STATUS
import io.sakurasou.hoshizora.constant.SETTING_SYSTEM
import io.sakurasou.hoshizora.controller.request.SiteSettingPatchRequest
import io.sakurasou.hoshizora.controller.request.SystemSettingPatchRequest
import io.sakurasou.hoshizora.exception.dao.MissingNecessaryColumnException
import io.sakurasou.hoshizora.exception.service.setting.ConfigTypeNotMatchException
import io.sakurasou.hoshizora.exception.service.setting.SettingUpdateFailedException
import io.sakurasou.hoshizora.model.DatabaseSingleton.dbQuery
import io.sakurasou.hoshizora.model.dao.setting.SettingDao
import io.sakurasou.hoshizora.model.dto.SettingUpdateDTO
import io.sakurasou.hoshizora.model.setting.SiteSetting
import io.sakurasou.hoshizora.model.setting.SystemSetting
import io.sakurasou.hoshizora.model.setting.SystemStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/13 15:00
 */
class SettingServiceImpl(
    private val settingDao: SettingDao,
) : SettingService {
    private var systemSetting: SystemSetting? = null

    override suspend fun updateSystemSetting(systemSetting: SystemSettingPatchRequest) {
        dbQuery {
            val oldSystemSetting =
                settingDao.getSettingByName(SETTING_SYSTEM)?.let {
                    if (it.config !is SystemSetting) throw ConfigTypeNotMatchException()
                    it.config
                } ?: throw MissingNecessaryColumnException()
            val systemSettingConfig =
                SystemSetting(
                    defaultGroupId = systemSetting.defaultGroupId ?: oldSystemSetting.defaultGroupId,
                    allowSignup = systemSetting.allowSignup ?: oldSystemSetting.allowSignup,
                    allowRandomFetch = systemSetting.allowRandomFetch ?: oldSystemSetting.allowRandomFetch,
                )
            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            val settingUpdateDTO =
                SettingUpdateDTO(
                    name = SETTING_SYSTEM,
                    config = systemSettingConfig,
                    updateTime = now,
                )
            runCatching {
                val influenceRowCnt = settingDao.updateSettingByName(settingUpdateDTO)
                if (influenceRowCnt < 1) throw MissingNecessaryColumnException()
                this.systemSetting = systemSettingConfig
            }.onFailure {
                if (it is MissingNecessaryColumnException) {
                    throw SettingUpdateFailedException(it)
                } else {
                    throw it
                }
            }
        }
    }

    override suspend fun updateSiteSetting(siteSetting: SiteSetting) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val settingUpdateDTO =
            SettingUpdateDTO(
                name = SETTING_SITE,
                config = siteSetting,
                updateTime = now,
            )
        runCatching {
            val influenceRowCnt = dbQuery { settingDao.updateSettingByName(settingUpdateDTO) }
            if (influenceRowCnt < 1) throw MissingNecessaryColumnException()
        }.onFailure {
            if (it is MissingNecessaryColumnException) {
                throw SettingUpdateFailedException(it)
            } else {
                throw it
            }
        }
    }

    override suspend fun updateSiteSetting(siteSettingPatch: SiteSettingPatchRequest) {
        dbQuery {
            val oldSiteSetting =
                settingDao.getSettingByName(SETTING_SITE)?.let {
                    if (it.config !is SiteSetting) throw ConfigTypeNotMatchException()
                    it.config
                } ?: throw MissingNecessaryColumnException()

            val siteSettingConfig =
                SiteSetting(
                    siteExternalUrl = siteSettingPatch.siteExternalUrl ?: oldSiteSetting.siteExternalUrl,
                    siteTitle = siteSettingPatch.siteTitle ?: oldSiteSetting.siteTitle,
                    siteSubtitle = siteSettingPatch.siteSubtitle ?: oldSiteSetting.siteSubtitle,
                    siteDescription = siteSettingPatch.siteDescription ?: oldSiteSetting.siteDescription,
                )
            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            val settingUpdateDTO =
                SettingUpdateDTO(
                    name = SETTING_SITE,
                    config = siteSettingConfig,
                    updateTime = now,
                )
            runCatching {
                val influenceRowCnt = settingDao.updateSettingByName(settingUpdateDTO)
                if (influenceRowCnt < 1) throw MissingNecessaryColumnException()
            }.onFailure {
                if (it is MissingNecessaryColumnException) {
                    throw SettingUpdateFailedException(it)
                } else {
                    throw it
                }
            }
        }
    }

    override suspend fun updateSystemStatus(systemStatus: SystemStatus) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val settingUpdateDTO =
            SettingUpdateDTO(
                name = SETTING_STATUS,
                config = systemStatus,
                updateTime = now,
            )
        runCatching {
            val influenceRowCnt = dbQuery { settingDao.updateSettingByName(settingUpdateDTO) }
            if (influenceRowCnt < 1) throw MissingNecessaryColumnException()
        }.onFailure {
            if (it is MissingNecessaryColumnException) {
                throw SettingUpdateFailedException(it)
            } else {
                throw it
            }
        }
    }

    override suspend fun getSystemSetting(): SystemSetting {
        this.systemSetting?.let { return it }
        return dbQuery {
            settingDao.getSettingByName(SETTING_SYSTEM)
        }?.let {
            if (it.config !is SystemSetting) throw ConfigTypeNotMatchException()
            it.config
        }.also { this.systemSetting = it }
            ?: throw MissingNecessaryColumnException()
    }

    override suspend fun getSiteSetting(): SiteSetting =
        dbQuery {
            settingDao.getSettingByName(SETTING_SITE)
        }?.let {
            if (it.config !is SiteSetting) throw ConfigTypeNotMatchException()
            it.config
        } ?: throw MissingNecessaryColumnException()

    override suspend fun getSystemStatus(): SystemStatus =
        dbQuery {
            settingDao.getSettingByName(SETTING_STATUS)
        }?.let {
            if (it.config !is SystemStatus) throw ConfigTypeNotMatchException()
            it.config
        } ?: throw MissingNecessaryColumnException()
}
