package io.sakurasou.hoshizora.controller.request

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:14
 */
@Serializable
@Name("SiteSettingPatchRequest")
data class SiteSettingPatchRequest(
    val siteExternalUrl: String? = null,
    val siteTitle: String? = null,
    val siteSubtitle: String? = null,
    val siteDescription: String? = null,
)

@Serializable
@Name("SystemSettingPatchRequest")
data class SystemSettingPatchRequest(
    val defaultGroupId: Long? = null,
    val allowSignup: Boolean? = null,
    val allowRandomFetch: Boolean? = null,
)
