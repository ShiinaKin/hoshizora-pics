package io.sakurasou.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 13:58
 */
@Serializable
data class UserInsertRequest(
    val username: String,
    val groupId: Long,
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
    val password: String? = null
)

@Serializable
data class UserPatchRequest(
    val groupName: String? = null,
    val email: String? = null,
    val password: String? = null
)
