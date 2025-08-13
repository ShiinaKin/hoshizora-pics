package io.sakurasou.hoshizora.controller.vo

import io.github.smiley4.schemakenerator.core.annotations.Name
import io.sakurasou.hoshizora.model.setting.SettingConfig
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/12 19:08
 */
@Serializable
@Name("SettingVO")
data class SettingVO(
    val name: String,
    val config: SettingConfig,
)
