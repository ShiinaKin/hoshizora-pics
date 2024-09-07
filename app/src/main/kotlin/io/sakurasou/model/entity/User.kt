package io.sakurasou.model.entity

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/7 13:38
 */
@Serializable
data class User(
    val id: Long,
    val name: String,
    val password: String,
    val email: String?,
    val createTime: LocalDateTime,
    val updateTime: LocalDateTime
)