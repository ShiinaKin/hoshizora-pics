package io.sakurasou.hoshizora.controller.vo

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 10:45
 */
@Serializable
data class RoleVO(
    val name: String,
    val displayName: String,
    val description: String? = null,
    val permissions: List<PermissionVO>,
)

@Serializable
data class RolePageVO(
    val name: String,
    val displayName: String,
    val description: String? = null,
)
