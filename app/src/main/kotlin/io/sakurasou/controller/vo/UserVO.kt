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
    val email: String,
    val createTime: LocalDateTime,
    val imageCount: Int,
    val totalImageSize: Double
)

@Serializable
data class UserPageVO(
    val id: Long,
    val username: String,
    val groupName: String,
    val imageCount: Int,
    val totalImageSize: Double
)