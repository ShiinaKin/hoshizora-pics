package io.sakurasou.hoshizora.model.dao.image

import io.sakurasou.hoshizora.model.dao.album.Albums
import io.sakurasou.hoshizora.model.dao.group.Groups
import io.sakurasou.hoshizora.model.dao.strategy.Strategies
import io.sakurasou.hoshizora.model.dao.user.Users
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/9/5 14:52
 */
object Images : LongIdTable() {
    val userId = long("user_id")
    val groupId = long("group_id")
    val albumId = long("album_id")
    val uniqueName = char("unique_name", 32).uniqueIndex()
    val displayName = varchar("display_name", 255)
    val description = varchar("description", 255).nullable()
    val path = varchar("path", 255)
    val strategyId = long("strategy_id")
    val originName = varchar("origin_name", 255)
    val mimeType = varchar("mime_type", 255)
    val extension = varchar("extension", 255)
    val size = long("size").check { it greater 0 }
    val width = integer("width").check { it greater 0 }
    val height = integer("height").check { it greater 0 }
    val md5 = char("md5", 32)
    val sha256 = char("sha256", 64)
    val isPrivate = bool("is_private")
    val isAllowedRandomFetch = bool("is_allowed_random_fetch").default(false)
    val createTime = datetime("create_time")

    init {
        foreignKey(userId to Users.id)
        foreignKey(groupId to Groups.id)
        foreignKey(albumId to Albums.id)
        foreignKey(strategyId to Strategies.id)
    }

    val columnMap =
        mapOf(
            "userId" to userId,
            "albumId" to albumId,
            "name" to displayName,
            "createTime" to createTime,
            "size" to size,
        )
}
