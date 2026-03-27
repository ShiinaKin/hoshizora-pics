package io.sakurasou.hoshizora.controller.vo

import io.sakurasou.hoshizora.model.group.GroupConfig
import io.sakurasou.hoshizora.model.group.ImageType
import kotlinx.datetime.LocalDateTime
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
    val strategyName: String,
    val roles: List<String>,
    val isSystemReserved: Boolean,
    val createTime: LocalDateTime,
)

@Serializable
data class GroupPageVO(
    val id: Long,
    val name: String,
    val strategyId: Long,
    val strategyName: String,
    val userCount: Long,
    val imageCount: Long,
    val imageSize: Double,
    val isSystemReserved: Boolean,
    val createTime: LocalDateTime,
)

@Serializable
data class GroupAllowedImageType(
    val allowedImageTypes: List<ImageType>,
)
