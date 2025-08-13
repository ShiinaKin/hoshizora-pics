package io.sakurasou.hoshizora.controller.vo

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 13:03
 */
@Serializable
@Name("ImageVO")
data class ImageVO(
    val id: Long,
    val ownerId: Long,
    val ownerName: String,
    val displayName: String,
    val albumId: Long,
    val albumName: String,
    val originName: String,
    val description: String?,
    val mimeType: String,
    val size: Double,
    val width: Int,
    val height: Int,
    val md5: String,
    val sha256: String,
    val isPrivate: Boolean,
    val isAllowedRandomFetch: Boolean,
    val createTime: LocalDateTime,
)

@Serializable
@Name("ImageManageVO")
data class ImageManageVO(
    val id: Long,
    val ownerId: Long,
    val ownerName: String,
    val groupId: Long,
    val groupName: String,
    val strategyId: Long,
    val strategyName: String,
    val strategyType: String,
    val displayName: String,
    val albumId: Long,
    val albumName: String,
    val originName: String,
    val description: String?,
    val mimeType: String,
    val size: Double,
    val width: Int,
    val height: Int,
    val md5: String,
    val sha256: String,
    val isPrivate: Boolean,
    val isAllowedRandomFetch: Boolean,
    val createTime: LocalDateTime,
)

@Serializable
@Name("ImagePageVO")
data class ImagePageVO(
    val id: Long,
    val displayName: String,
    val isPrivate: Boolean,
    val externalUrl: String,
    val createTime: LocalDateTime,
)

@Serializable
@Name("ImageManagePageVO")
data class ImageManagePageVO(
    val id: Long,
    val displayName: String,
    val userId: Long,
    val username: String,
    val userEmail: String,
    val isPrivate: Boolean,
    val externalUrl: String,
    val createTime: LocalDateTime,
)
