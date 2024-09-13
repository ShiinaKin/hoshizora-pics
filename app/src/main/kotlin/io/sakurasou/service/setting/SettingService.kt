package io.sakurasou.service.setting

import io.sakurasou.controller.request.SiteSettingPatchRequest
import io.sakurasou.controller.request.StrategySettingPatchRequest
import io.sakurasou.controller.request.SystemSettingPatchRequest
import io.sakurasou.model.setting.SiteSetting
import io.sakurasou.model.setting.StrategySetting
import io.sakurasou.model.setting.SystemSetting

/**
 * @author Shiina Kin
 * 2024/9/13 14:57
 */
interface SettingService {
    suspend fun updateSystemSetting(systemSetting: SystemSettingPatchRequest)
    suspend fun updateSiteSetting(siteSetting: SiteSettingPatchRequest)
    suspend fun updateStrategySetting(strategySetting: StrategySettingPatchRequest)
    suspend fun getSystemSetting(): SystemSetting
    suspend fun getSiteSetting(): SiteSetting
    suspend fun getStrategySetting(): StrategySetting
}