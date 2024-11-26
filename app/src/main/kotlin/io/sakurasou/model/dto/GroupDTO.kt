package io.sakurasou.model.dto

import io.sakurasou.model.group.GroupConfig
import kotlinx.datetime.LocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/12 13:46
 */
data class GroupInsertDTO(
    val name: String,
    val description: String?,
    val strategyId: Long,
    val config: GroupConfig,
    val createTime: LocalDateTime
)

data class GroupUpdateDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val strategyId: Long,
    val config: GroupConfig
)