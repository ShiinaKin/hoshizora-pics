package io.sakurasou.model.entity

import io.sakurasou.model.setting.SettingConfig
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/7 13:34
 */
@Serializable
data class Setting(
    val id: Long,
    val name: String,
    val config: SettingConfig,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
)