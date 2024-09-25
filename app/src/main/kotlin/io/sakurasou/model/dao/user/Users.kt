package io.sakurasou.model.dao.user

import io.sakurasou.model.dao.album.Albums
import io.sakurasou.model.dao.group.Groups
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/9/7 13:41
 */
object Users : LongIdTable("users") {
    val groupId = long("group_id")
    val name = varchar("name", 50).uniqueIndex()
    val password = char("password", 60)
    val email = varchar("email", 255).nullable()
    val isDefaultImagePrivate = bool("is_default_image_private")
    val defaultAlbumId = long("default_album_id").nullable()
    val isBanned = bool("is_banned").default(false)
    val updateTime = datetime("update_time")
    val createTime = datetime("create_time")

    init {
        foreignKey(groupId to Groups.id)
        foreignKey(defaultAlbumId to Albums.id)
    }
}