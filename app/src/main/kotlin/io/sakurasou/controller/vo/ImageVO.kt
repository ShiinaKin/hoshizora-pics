package io.sakurasou.controller.vo

import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 13:03
 */
@Serializable
data class ImageVO(
    val id: Long,
    val name: String,
    val albumName: String,
    val originName: String,
    val description: String?,
    val mimeType: String,
    val extension: String,
    val size: Double,
    val width: Int,
    val height: Int,
    val md5: String,
    val sha1: String,
)

@Serializable
data class ImagePageVO(
    val id: Long,
    val originName: String,
    val externalUrl: String,
    val size: Double
)