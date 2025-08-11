package io.sakurasou.model.entity

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/11/14 22:06
 */
@Serializable
data class PersonalAccessToken(
    val id: Long,
    val userId: Long,
    val name: String,
    val description: String?,
    val createTime: LocalDateTime,
    val expireTime: LocalDateTime,
)
