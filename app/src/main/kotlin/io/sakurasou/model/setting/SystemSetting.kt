package io.sakurasou.model.setting

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/12 17:11
 */
@Serializable
data class SystemSetting(
    val defaultGroupId: Long,
    val allowSignup: Boolean
) : SettingConfig()