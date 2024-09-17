package io.sakurasou.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 13:58
 */
@Serializable
data class UserInsertRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class UserLoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class UserSelfPatchRequest(
    val email: String? = null,
    val password: String? = null,
    val isDefaultImagePrivate: Boolean? = null,
    val defaultAlbumId: Long? = null
)

@Serializable
data class UserManagePatchRequest(
    val groupId: Long? = null,
    val email: String? = null,
    val password: String? = null,
    val isDefaultImagePrivate: Boolean? = null,
    val defaultAlbumId: Long? = null
)

@Serializable
data class UserManageInsertRequest(
    val groupId: Long,
    val username: String,
    val password: String,
    val email: String,
    val isDefaultImagePrivate: Boolean,
    val defaultAlbumId: Long
)