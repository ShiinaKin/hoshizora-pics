package io.sakurasou.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:14
 */
@Serializable
data class SiteSettingPatchRequest(
    val siteTitle: String? = null,
    val siteSubtitle: String? = null,
    val siteKeyword: String? = null,
    val siteDescription: String? = null,
    val homePageRandomPicDisplay: Boolean? = null
)

data class StrategySettingPatchRequest(
    val allowedImageTypes: List<String>
)