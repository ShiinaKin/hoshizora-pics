package io.sakurasou.controller.vo

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
    val description: String? = null,
    val permissions: List<PermissionVO>
)