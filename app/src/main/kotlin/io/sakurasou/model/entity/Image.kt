package io.sakurasou.model.entity

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/5 14:44
 */
@Serializable
data class Image(
    val id: Long,
    val userId: Long,
    val groupId: Long,
    val albumId: Long,
    val uniqueName: String,
    val displayName: String,
    val description: String? = null,
    val path: String,
    val strategyId: Long,
    val originName: String,
    val mimeType: String,
    val extension: String,
    val size: Long,
    val width: Int,
    val height: Int,
    val md5: String,
    val sha256: String,
    val isPrivate: Boolean,
    val createTime: LocalDateTime
)