package io.sakurasou.hoshizora.model.dao.relation

import io.sakurasou.hoshizora.model.dao.permission.Permissions
import io.sakurasou.hoshizora.model.dao.role.Roles
import org.jetbrains.exposed.sql.Table

/**
 * @author ShiinaKin
 * 2024/9/7 15:20
 */
object RolePermissions : Table("role_permissions") {
    val roleName = varchar("role_name", 255)
    val permissionName = varchar("permission_name", 255)

    override val primaryKey = PrimaryKey(roleName, permissionName)

    init {
        foreignKey(roleName to Roles.name)
        foreignKey(permissionName to Permissions.name)
    }
}
