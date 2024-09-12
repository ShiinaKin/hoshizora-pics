package io.sakurasou.model.dto

import kotlinx.datetime.LocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/12 12:51
 */
data class UserInsertDTO(
    val groupId: Long,
    val username: String,
    val password: String,
    val email: String?,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
)