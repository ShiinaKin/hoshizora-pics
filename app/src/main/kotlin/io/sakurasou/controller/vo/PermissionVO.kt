package io.sakurasou.controller.vo

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/18 17:35
 */
@Serializable
@Name("PermissionVO")
data class PermissionVO(
    val name: String,
    val description: String?,
)
