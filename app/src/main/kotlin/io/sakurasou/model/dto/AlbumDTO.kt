package io.sakurasou.model.dto

import kotlinx.datetime.LocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/12 15:43
 */
data class AlbumInsertDTO(
    val userId: Long,
    val name: String,
    val description: String? = null,
    val imageCount: Int,
    val isUncategorized: Boolean,
    val createTime: LocalDateTime
)

data class AlbumUpdateDTO(
    val id: Long,
    val userId: Long,
    val name: String,
    val description: String? = null,
)