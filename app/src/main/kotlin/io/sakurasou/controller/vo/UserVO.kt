package io.sakurasou.controller.vo

import io.github.smiley4.schemakenerator.core.annotations.Name
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * @author Shiina Kin
 * 2024/9/9 13:48
 */
@Serializable
@Name("UserVO")
data class UserVO(
    val id: Long,
    val username: String,
    val groupId: Long,
    val groupName: String,
    val email: String,
    val isDefaultImagePrivate: Boolean,
    val isBanned: Boolean,
    val createTime: LocalDateTime,
    val imageCount: Long,
    val albumCount: Long,
    val totalImageSize: Double,
    val allSize: Double,
)

@Serializable
@Name("UserPageVO")
data class UserPageVO(
    val id: Long,
    val username: String,
    val groupName: String,
    val isBanned: Boolean,
    val createTime: LocalDateTime,
    val imageCount: Long,
    val albumCount: Long,
    val totalImageSize: Double,
)
