package io.sakurasou.hoshizora.service.setting

import io.sakurasou.hoshizora.controller.request.SiteSettingPatchRequest
import io.sakurasou.hoshizora.controller.request.SystemSettingPatchRequest
import io.sakurasou.hoshizora.model.setting.SiteSetting
import io.sakurasou.hoshizora.model.setting.SystemSetting
import io.sakurasou.hoshizora.model.setting.SystemStatus

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
