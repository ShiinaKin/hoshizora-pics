package io.sakurasou.hoshizora.model.dto

import io.sakurasou.hoshizora.model.setting.SettingConfig
import kotlinx.datetime.LocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/12 16:03
 */
data class SettingInsertDTO(
    val name: String,
    val config: SettingConfig,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime,
)

data class SettingUpdateDTO(
    val name: String,
    val config: SettingConfig,
    val updateTime: LocalDateTime,
)
