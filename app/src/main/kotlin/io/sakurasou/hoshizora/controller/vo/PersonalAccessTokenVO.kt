package io.sakurasou.hoshizora.controller.vo

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/11/16 06:01
 */

@Serializable
data class PersonalAccessTokenPageVO(
    val id: Long,
    val name: String,
    val description: String?,
    val createTime: LocalDateTime,
    val expireTime: LocalDateTime,
    val isExpired: Boolean,
)
