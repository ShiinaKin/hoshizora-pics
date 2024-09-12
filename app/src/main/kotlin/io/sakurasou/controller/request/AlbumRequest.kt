package io.sakurasou.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:53
 */
@Serializable
data class AlbumInsertRequest(
    val name: String,
    val description: String
)

@Serializable
data class AlbumPatchRequest(
    val name: String? = null,
    val description: String? = null
)