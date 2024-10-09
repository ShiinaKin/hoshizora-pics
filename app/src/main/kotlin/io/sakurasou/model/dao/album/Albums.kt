package io.sakurasou.model.dao.album

import io.sakurasou.model.dao.image.Images
import io.sakurasou.model.dao.user.Users
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/9/7 13:45
 */
object Albums : LongIdTable("albums") {
    val userId = long("user_id")
    val name = varchar("name", 255)
    val description = varchar("description", 255).nullable()
    val isUncategorized = bool("is_uncategorized")
    val createTime = datetime("create_time")

    init {
        uniqueIndex(userId, name)
        foreignKey(userId to Users.id)
    }

    val columnMap = mapOf(
        "name" to name,
        "createTime" to createTime,
        "imageCount" to Images.id.count()
    )
}