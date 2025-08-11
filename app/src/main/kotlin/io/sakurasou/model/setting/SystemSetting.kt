package io.sakurasou.model.setting

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/12 17:11
 */
@Serializable
@Name("SystemSetting")
data class SystemSetting(
    val defaultGroupId: Long,
    val allowSignup: Boolean,
    val allowRandomFetch: Boolean,
) : SettingConfig()
