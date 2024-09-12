package io.sakurasou.model.dao.user

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
    val createTime = datetime("create_time")
    val updateTime = datetime("update_time")

    init {
        foreignKey(groupId to Groups.id)
    }
}