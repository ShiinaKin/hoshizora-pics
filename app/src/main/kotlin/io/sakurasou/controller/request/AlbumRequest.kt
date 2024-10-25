package io.sakurasou.controller.request

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 12:53
 */
@Serializable
@Name("AlbumSelfInsertRequest")
data class AlbumSelfInsertRequest(
    val name: String,
    val description: String
)

@Serializable
@Name("AlbumManageInsertRequest")
data class AlbumManageInsertRequest(
    val userId: Long,
    val name: String,
    val description: String
)

@Serializable
@Name("AlbumSelfPatchRequest")
data class AlbumSelfPatchRequest(
    val name: String? = null,
    val description: String? = null
)

@Serializable
@Name("AlbumManagePatchRequest")
data class AlbumManagePatchRequest(
    val userId: Long? = null,
    val name: String? = null,
    val description: String? = null
)