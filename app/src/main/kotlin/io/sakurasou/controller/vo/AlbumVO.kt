package io.sakurasou.controller.vo

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 10:25
 */
@Serializable
@Name("AlbumVO")
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
@Name("AlbumManageVO")
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
@Name("AlbumPageVO")
data class AlbumPageVO(
    val id: Long,
    val name: String,
    val imageCount: Long,
    val isUncategorized: Boolean,
    val isDefault: Boolean,
    val createTime: LocalDateTime,
)

@Serializable
@Name("AlbumManagePageVO")
data class AlbumManagePageVO(
    val id: Long,
    val name: String,
    val userId: Long,
    val username: String,
    val userEmail: String,
    val imageCount: Long,
    val createTime: LocalDateTime,
)
