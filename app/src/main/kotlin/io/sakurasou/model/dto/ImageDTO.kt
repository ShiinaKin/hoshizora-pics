package io.sakurasou.model.dto

import kotlinx.datetime.LocalDateTime

/**
 * @author ShiinaKin
 * 2024/9/5 14:44
 */
data class ImageInsertDTO(
    val userId: Long,
    val groupId: Long,
    val albumId: Long,
    val name: String,
    val description: String? = null,
    val path: String,
    val strategyId: Long,
    val originName: String,
    val mimeType: String,
    val extension: String,
    val size: Double,
    val width: Int,
    val height: Int,
    val md5: String,
    val sha1: String,
    val isPrivate: Boolean,
    val createTime: LocalDateTime
)

data class ImageCountAndTotalSizeDTO(
    val count: Long,
    val totalSize: Double
)