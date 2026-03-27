package io.sakurasou.hoshizora.controller.vo

import io.sakurasou.hoshizora.model.setting.SettingConfig
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/12 19:08
 */
@Serializable
data class SettingVO(
    val name: String,
    val config: SettingConfig,
)
