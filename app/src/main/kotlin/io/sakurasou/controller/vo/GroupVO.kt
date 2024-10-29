package io.sakurasou.controller.vo

import io.github.smiley4.schemakenerator.core.annotations.Name
import io.sakurasou.model.group.GroupConfig
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
    val roles: List<String>
)

@Serializable
@Name("GroupPageVO")
data class GroupPageVO(
    val id: Long,
    val name: String,
    val strategyId: Long,
    val totalImageCount: Long,
    val totalImageSize: Double
)

@Serializable
@Name("GroupAllowedImageType")
data class GroupAllowedImageType(
    val allowedImageTypes: Set<String>
)