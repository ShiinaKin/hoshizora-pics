package io.sakurasou.model.dto

import kotlinx.datetime.LocalDateTime

/**
 * @author ShiinaKin
 * 2024/11/16 02:11
 */

data class PersonalAccessTokenInsertDTO(
    val userId: Long,
    val name: String,
    val description: String?,
    val createTime: LocalDateTime,
    val expireTime: LocalDateTime,
)

data class PersonalAccessTokenUpdateDTO(
    val id: Long,
    val name: String,
    val description: String?,
)
