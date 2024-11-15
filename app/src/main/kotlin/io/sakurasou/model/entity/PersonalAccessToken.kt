package io.sakurasou.model.entity

import kotlinx.datetime.LocalDateTime

/**
 * @author ShiinaKin
 * 2024/11/14 22:06
 */
data class PersonalAccessToken(
    val id: Long,
    val userId: Long,
    val token: String,
    val name: String,
    val description: String?,
    val createTime: LocalDateTime,
    val expireTime: LocalDateTime
)