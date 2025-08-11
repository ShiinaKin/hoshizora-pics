package io.sakurasou.model.setting

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 15:10
 */
@Serializable
@Name("SettingConfigSealed")
sealed class SettingConfig
