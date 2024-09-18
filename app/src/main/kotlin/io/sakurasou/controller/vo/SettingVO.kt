package io.sakurasou.controller.vo

import io.sakurasou.model.setting.SettingConfig
import kotlinx.datetime.LocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/12 19:08
 */
data class SettingVO(
    val name: String,
    val config: SettingConfig
)