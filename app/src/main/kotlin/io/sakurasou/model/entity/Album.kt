package io.sakurasou.model.entity

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/7 13:31
 */
@Serializable
data class Album(
    val id: Long,
    val userId: Long,
    val name: String,
    val description: String? = null,
    val imageCount: Int,
    val createTime: LocalDateTime
)