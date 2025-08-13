package io.sakurasou.hoshizora.model.dto

import kotlinx.datetime.LocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/12 16:25
 */
data class RoleInsertDTO(
    val name: String,
    val displayName: String,
    val isSystemReserved: Boolean,
    val description: String?,
    val createTime: LocalDateTime,
)

data class RoleUpdateDTO(
    val displayName: String,
    val description: String?,
)
