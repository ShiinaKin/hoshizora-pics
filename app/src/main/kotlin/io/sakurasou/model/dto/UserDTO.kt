package io.sakurasou.model.dto

import kotlinx.datetime.LocalDateTime

/**
 * @author Shiina Kin
 * 2024/9/12 12:51
 */
data class UserInsertDTO(
    val groupId: Long,
    val username: String,
    val password: String,
    val email: String,
    val isDefaultImagePrivate: Boolean,
    val defaultAlbumId: Long?,
    val isBanned: Boolean,
    val updateTime: LocalDateTime,
    val createTime: LocalDateTime
)

data class UserSelfUpdateDTO(
    val id: Long,
    val password: String,
    val email: String,
    val isDefaultImagePrivate: Boolean,
    val defaultAlbumId: Long?,
    val updateTime: LocalDateTime
)

data class UserManageUpdateDTO(
    val id: Long,
    val groupId: Long,
    val password: String,
    val email: String,
    val isDefaultImagePrivate: Boolean,
    val defaultAlbumId: Long?,
    val updateTime: LocalDateTime
)