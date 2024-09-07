package io.sakurasou.model.dao.album

import io.sakurasou.model.dao.user.Users
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

/**
 * @author ShiinaKin
 * 2024/9/7 13:45
 */
object Albums : Table("ablums") {
    val id = long("id").autoIncrement()
    val userId = long("user_id")
    val name = varchar("name", 255)
    val description = varchar("description", 255).nullable()
    val imageCount = integer("image_count").check { it greaterEq 0 }
    val createTime = datetime("create_time")

    override val primaryKey = PrimaryKey(id)

    init {
        foreignKey(userId to Users.id)
    }
}