package io.sakurasou.controller.vo

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 13:48
 */
@Serializable
data class UserVO(
    val id: Long,
    val username: String,
    val groupName: String,
    val email: String?,
    val isDefaultImagePrivate: Boolean,
    val isBanned: Boolean,
    val createTime: LocalDateTime,
    val imageCount: Long,
    val totalImageSize: Long
)

@Serializable
data class UserPageVO(
    val id: Long,
    val username: String,
    val groupName: String,
    val isBanned: Boolean,
    val createTime: LocalDateTime,
    val imageCount: Long,
    val totalImageSize: Long
)