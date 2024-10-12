package io.sakurasou.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:14
 */
@Serializable
data class SiteSettingPatchRequest(
    val siteExternalUrl: String? = null,
    val siteTitle: String? = null,
    val siteSubtitle: String? = null,
    val siteKeyword: String? = null,
    val siteDescription: String? = null,
    val homePageRandomPicDisplay: Boolean? = null
)

@Serializable
data class StrategySettingPatchRequest(
    val allowedImageTypes: List<String>
)

@Serializable
data class SystemSettingPatchRequest(
    val defaultGroupId: Long? = null,
    val allowSignup: Boolean? = null
)