package io.sakurasou.hoshizora.controller.vo

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 10:25
 */
@Serializable
data class AlbumVO(
    val id: Long,
    val name: String,
    val description: String? = null,
    val imageCount: Long,
    val isUncategorized: Boolean,
    val isDefault: Boolean,
    val createTime: LocalDateTime,
)

@Serializable
data class AlbumManageVO(
    val id: Long,
    val name: String,
    val userId: Long,
    val username: String,
    val description: String? = null,
    val imageCount: Long,
    val isUncategorized: Boolean,
    val isDefault: Boolean,
    val createTime: LocalDateTime,
)

@Serializable
data class AlbumPageVO(
    val id: Long,
    val name: String,
    val imageCount: Long,
    val isUncategorized: Boolean,
    val isDefault: Boolean,
    val createTime: LocalDateTime,
)

@Serializable
data class AlbumManagePageVO(
    val id: Long,
    val name: String,
    val userId: Long,
    val username: String,
    val userEmail: String,
    val imageCount: Long,
    val createTime: LocalDateTime,
)
