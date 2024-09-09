package io.sakurasou.model.setting

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 09:29
 */
@Serializable
data class StrategySetting(
    val allowedImageTypes: List<String>
) : SettingConfig()
