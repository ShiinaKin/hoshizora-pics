package io.sakurasou.controller.vo

import io.sakurasou.model.group.GroupConfig
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
    val groupConfig: GroupConfig,
    val strategyId: Long,
    val roles: List<String>
)

@Serializable
data class GroupPageVO(
    val id: Long,
    val name: String,
    val strategyId: Long,
    val totalImageCount: Long,
    val totalImageSize: Double
)