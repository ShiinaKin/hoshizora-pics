package io.sakurasou.model.dao.relation

import io.sakurasou.model.dao.group.Groups
import io.sakurasou.model.dao.user.Users
import org.jetbrains.exposed.sql.Table

/**
 * @author ShiinaKin
 * 2024/9/7 15:18
 */
object UserGroups : Table("user_groups") {
    val userId = long("user_id")
    val groupId = long("group_id")

    override val primaryKey = PrimaryKey(userId, groupId)

    init {
        foreignKey(userId to Users.id)
        foreignKey(groupId to Groups.id)
    }
}