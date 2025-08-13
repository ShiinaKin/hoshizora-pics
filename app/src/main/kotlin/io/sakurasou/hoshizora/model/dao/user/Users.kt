package io.sakurasou.hoshizora.model.dao.user

import io.sakurasou.hoshizora.model.dao.album.Albums
import io.sakurasou.hoshizora.model.dao.group.Groups
import io.sakurasou.hoshizora.model.dao.image.Images
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.longLiteral
import org.jetbrains.exposed.sql.sum

/**
 * @author ShiinaKin
 * 2024/9/7 13:41
 */
object Users : LongIdTable("users") {
    val groupId = long("group_id")
    val name = varchar("name", 50).uniqueIndex()
    val password = char("password", 60)
    val email = varchar("email", 255)
    val isDefaultImagePrivate = bool("is_default_image_private")
    val defaultAlbumId = long("default_album_id").nullable()
    val isBanned = bool("is_banned").default(false)
    val updateTime = datetime("update_time")
    val createTime = datetime("create_time")

    init {
        foreignKey(groupId to Groups.id)
        foreignKey(defaultAlbumId to Albums.id)
    }

    val columnMap =
        mapOf(
            "createTime" to createTime,
            "imageCount" to Images.id.count(),
            "albumCount" to Albums.id.count(),
            "totalImageSize" to Coalesce(Images.size.sum(), longLiteral(0)),
        )
}
