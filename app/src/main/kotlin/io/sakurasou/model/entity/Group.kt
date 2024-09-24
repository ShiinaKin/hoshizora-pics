package io.sakurasou.model.entity

import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/7 13:39
 */
@Serializable
data class Group(
    val id: Long,
    val name: String,
    val description: String?,
    val strategyId: Long,
    val maxSize: Long = 5 * 1024 * 1024 * 1024L
)