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
    val roleName = varchar("role_name", 255)

    override val primaryKey = PrimaryKey(groupId, roleName)

    init {
        foreignKey(groupId to Groups.id)
        foreignKey(roleName to Roles.name)
    }
}
