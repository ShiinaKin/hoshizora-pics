package io.sakurasou.hoshizora.model.setting

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/13 15:58
 */
@Serializable
@Name("SystemStatus")
data class SystemStatus(
    val isInit: Boolean,
    val version: String,
) : SettingConfig()
