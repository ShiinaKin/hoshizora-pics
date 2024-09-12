package io.sakurasou.model.dao.image

import io.sakurasou.model.dao.album.Albums
import io.sakurasou.model.dao.group.Groups
import io.sakurasou.model.dao.strategy.Strategies
import io.sakurasou.model.dao.user.Users
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
    val name = char("name", 32).uniqueIndex()
    val description = varchar("description", 255).nullable()
    val path = varchar("path", 255)
    val strategyId = long("strategy_id")
    val originName = varchar("origin_name", 255)
    val mimeType = varchar("mime_type", 255)
    val extension = varchar("extension", 255)
    val size = double("size").check { it greater 0.0 }
    val width = integer("width").check { it greater 0 }
    val height = integer("height").check { it greater 0 }
    val md5 = char("md5", 32)
    val sha1 = char("sha1", 40)
    val createTime = datetime("create_time")

    init {
        foreignKey(userId to Users.id)
        foreignKey(groupId to Groups.id)
        foreignKey(albumId to Albums.id)
        foreignKey(strategyId to Strategies.id)
    }
}