package io.sakurasou.controller.vo

import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/18 17:35
 */
@Serializable
data class PermissionVO(
    val name: String,
    val description: String?
)