package io.sakurasou.hoshizora.model.entity

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/7 13:40
 */
@Serializable
data class Role(
    val name: String,
    val displayName: String,
    val isSystemReserved: Boolean,
    val description: String?,
    val createTime: LocalDateTime,
)
