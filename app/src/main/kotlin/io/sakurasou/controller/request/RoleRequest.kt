package io.sakurasou.controller.request

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/12/2 17:41
 */

@Serializable
@Name("RoleInsertRequest")
data class RoleInsertRequest(
    val name: String,
    val displayName: String,
    val description: String? = null,
    val permissions: List<String>
)

@Serializable
@Name("RolePatchRequest")
data class RolePatchRequest(
    val displayName: String? = null,
    val description: String? = null
)