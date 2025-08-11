package io.sakurasou.controller.request

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 13:58
 */
@Serializable
@Name("UserInsertRequest")
data class UserInsertRequest(
    val username: String,
    val email: String,
    val password: String,
)

@Serializable
@Name("UserLoginRequest")
data class UserLoginRequest(
    val username: String,
    val password: String,
)

@Serializable
@Name("UserSelfPatchRequest")
data class UserSelfPatchRequest(
    val email: String? = null,
    val password: String? = null,
    val isDefaultImagePrivate: Boolean? = null,
    val defaultAlbumId: Long? = null,
)

@Serializable
@Name("UserManagePatchRequest")
data class UserManagePatchRequest(
    val groupId: Long? = null,
    val email: String? = null,
    val password: String? = null,
    val isDefaultImagePrivate: Boolean? = null,
    val defaultAlbumId: Long? = null,
)

@Serializable
@Name("UserManageInsertRequest")
data class UserManageInsertRequest(
    val groupId: Long,
    val username: String,
    val password: String,
    val email: String,
    val isDefaultImagePrivate: Boolean,
)
