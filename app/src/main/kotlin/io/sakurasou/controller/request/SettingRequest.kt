package io.sakurasou.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:14
 */
@Serializable
data class SiteSettingPatchRequest(
    val siteTitle: String,
    val siteSubtitle: String,
    val siteKeyword: String,
    val siteDescription: String,
    val homePageRandomPicDisplay: Boolean
)

@Serializable
data class StrategySettingPatchRequest(
    val allowedImageTypes: List<String>
)

@Serializable
data class SystemSettingPatchRequest(
    val defaultGroupId: Long,
    val allowSignup: Boolean
)