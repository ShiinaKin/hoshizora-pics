package io.sakurasou.model.setting

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/13 15:58
 */
@Serializable
data class SystemStatus(
    val isInit: Boolean
) : SettingConfig()