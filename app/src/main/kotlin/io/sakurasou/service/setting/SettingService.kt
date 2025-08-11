package io.sakurasou.service.setting

import io.sakurasou.controller.request.SiteSettingPatchRequest
import io.sakurasou.controller.request.SystemSettingPatchRequest
import io.sakurasou.model.setting.SiteSetting
import io.sakurasou.model.setting.SystemSetting
import io.sakurasou.model.setting.SystemStatus

/**
 * @author Shiina Kin
 * 2024/9/13 14:57
 */
interface SettingService {
    suspend fun updateSystemSetting(systemSetting: SystemSettingPatchRequest)

    suspend fun updateSiteSetting(siteSetting: SiteSetting)

    suspend fun updateSiteSetting(siteSettingPatch: SiteSettingPatchRequest)

    suspend fun updateSystemStatus(systemStatus: SystemStatus)

    suspend fun getSystemSetting(): SystemSetting

    suspend fun getSiteSetting(): SiteSetting

    suspend fun getSystemStatus(): SystemStatus
}
