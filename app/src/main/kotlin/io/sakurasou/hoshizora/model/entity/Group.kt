package io.sakurasou.hoshizora.model.entity

import io.sakurasou.hoshizora.model.group.GroupConfig
import kotlinx.datetime.LocalDateTime
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
    val config: GroupConfig,
    val isSystemReserved: Boolean,
    val createTime: LocalDateTime,
)
