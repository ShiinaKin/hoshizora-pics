package io.sakurasou.model.dao.relation

import io.sakurasou.model.dao.permission.Permissions
import io.sakurasou.model.dao.role.Roles
import org.jetbrains.exposed.sql.Table

/**
 * @author ShiinaKin
 * 2024/9/7 15:20
 */
object RolePermissions: Table("role_permissions") {
    val roleId = long("role_id")
    val permissionId = long("permission_id")

    override val primaryKey = PrimaryKey(roleId, permissionId)

    init {
        foreignKey(roleId to Roles.id)
        foreignKey(permissionId to Permissions.id)
    }
}