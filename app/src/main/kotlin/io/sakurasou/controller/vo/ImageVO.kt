package io.sakurasou.controller.vo

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 13:03
 */
@Serializable
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
    val createTime: LocalDateTime
)

@Serializable
data class ImageManageVO(
    val id: Long,
    val ownerId: Long,
    val ownerName: String,
    val groupId: Long,
    val groupName: String,
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
    val createTime: LocalDateTime
)

@Serializable
data class ImagePageVO(
    val id: Long,
    val originName: String,
    val externalUrl: String,
    val size: Double,
    val isPrivate: Boolean
)