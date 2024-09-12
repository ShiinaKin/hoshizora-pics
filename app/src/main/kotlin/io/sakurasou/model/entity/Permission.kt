package io.sakurasou.model.entity

import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/7 13:40
 */
@Serializable
data class Permission(
    val name: String,
    val description: String?
)