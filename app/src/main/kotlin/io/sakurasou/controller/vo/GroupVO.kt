package io.sakurasou.controller.vo

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 10:28
 */
@Serializable
data class GroupVO(
    val id: Long,
    val name: String,
    val description: String?,
    val strategyId: Long,
    val maxSize: Double = 5 * 1024 * 1024 * 1024.0,
    val roles: List<String>
)

@Serializable
data class GroupPageVO(
    val id: Long,
    val name: String,
    val strategyId: Long
)