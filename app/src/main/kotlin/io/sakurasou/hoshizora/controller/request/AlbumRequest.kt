package io.sakurasou.hoshizora.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:53
 */
@Serializable
data class AlbumSelfInsertRequest(
    val name: String,
    val description: String? = null,
)

@Serializable
data class AlbumManageInsertRequest(
    val userId: Long,
    val name: String,
    val description: String? = null,
)

@Serializable
data class AlbumSelfPatchRequest(
    val name: String? = null,
    val description: String? = null,
    val isDefault: Boolean? = null,
)

@Serializable
data class AlbumManagePatchRequest(
    val userId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val isDefault: Boolean? = null,
)
