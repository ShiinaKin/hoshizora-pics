package io.sakurasou.model.dao.relation

import io.sakurasou.model.dao.group.Groups
import io.sakurasou.model.dao.role.Roles
import org.jetbrains.exposed.sql.Table

/**
 * @author ShiinaKin
 * 2024/9/7 15:19
 */
object GroupRoles : Table("group_roles") {
    val groupId = long("group_id")
    val roleId = long("role_id")

    override val primaryKey = PrimaryKey(groupId, roleId)

    init {
        foreignKey(groupId to Groups.id)
        foreignKey(roleId to Roles.id)
    }
}