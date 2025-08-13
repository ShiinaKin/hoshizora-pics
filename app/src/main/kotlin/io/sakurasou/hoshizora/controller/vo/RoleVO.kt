package io.sakurasou.hoshizora.controller.vo

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 10:45
 */
@Serializable
@Name("RoleVO")
data class RoleVO(
    val name: String,
    val displayName: String,
    val description: String? = null,
    val permissions: List<PermissionVO>,
)

@Serializable
@Name("RolePageVO")
data class RolePageVO(
    val name: String,
    val displayName: String,
    val description: String? = null,
)
