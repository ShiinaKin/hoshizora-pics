package io.sakurasou.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 13:36
 */
@Serializable
data class ImagePatchRequest(
    val originName: String? = null,
    val description: String? = null
)