package io.sakurasou.hoshizora.controller.request

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/12/2 17:41
 */

@Serializable
data class RoleInsertRequest(
    val name: String,
    val displayName: String,
    val description: String? = null,
    val permissions: List<String>,
)

@Serializable
data class RolePatchRequest(
    val displayName: String? = null,
    val description: String? = null,
)
