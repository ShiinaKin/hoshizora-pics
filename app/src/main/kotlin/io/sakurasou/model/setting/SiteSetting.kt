package io.sakurasou.model.setting

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 09:29
 */
@Serializable
@Name("SiteSetting")
data class SiteSetting(
    val siteExternalUrl: String,
    val siteTitle: String,
    val siteSubtitle: String,
    val siteDescription: String,
) : SettingConfig()
