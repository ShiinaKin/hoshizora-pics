package io.sakurasou.controller.vo

import io.github.smiley4.schemakenerator.core.annotations.Name
import io.sakurasou.model.group.GroupConfig
import io.sakurasou.model.group.ImageType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 10:28
 */
@Serializable
@Name("GroupVO")
data class GroupVO(
    val id: Long,
    val name: String,
    val description: String?,
    val groupConfig: GroupConfig,
    val strategyId: Long,
    val strategyName: String,
    val roles: List<String>,
    val isSystemReserved: Boolean,
    val createTime: LocalDateTime
)

@Serializable
@Name("GroupPageVO")
data class GroupPageVO(
    val id: Long,
    val name: String,
    val strategyId: Long,
    val strategyName: String,
    val userCount: Long,
    val imageCount: Long,
    val imageSize: Double,
    val isSystemReserved: Boolean,
    val createTime: LocalDateTime
)

@Serializable
@Name("GroupAllowedImageType")
data class GroupAllowedImageType(
    val allowedImageTypes: List<ImageType>
)