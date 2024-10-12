package io.sakurasou.model.setting

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 09:29
 */
@Serializable
data class SiteSetting(
    val siteExternalUrl: String,
    val siteTitle: String,
    val siteSubtitle: String,
    val siteKeyword: String,
    val siteDescription: String,
    val homePageRandomPicDisplay: Boolean
) : SettingConfig()
