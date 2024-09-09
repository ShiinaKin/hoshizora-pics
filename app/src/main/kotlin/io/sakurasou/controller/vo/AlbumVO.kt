package io.sakurasou.controller.vo

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
    val imageCount: Int,
)

@Serializable
data class AlbumPageVO(
    val id: Long,
    val name: String,
    val imageCount: Int,
)