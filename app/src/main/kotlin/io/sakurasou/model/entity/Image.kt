package io.sakurasou.model.entity

import kotlinx.serialization.Serializable

/**
 * @author ShiinaKin
 * 2024/9/5 14:44
 */
@Serializable
data class Image(
    val id: Int,
    val name: String,
    val url: String,
    val size: Int,
    val type: String,
    val createTime: Long,
    val updateTime: Long
)