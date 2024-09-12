package io.sakurasou.model.dao.relation

import org.jetbrains.exposed.sql.batchInsert

/**
 * @author ShiinaKin
 * 2024/9/7 15:34
 */
class RelationDaoImpl : RelationDao {
    override fun batchInsertGroupToRoles(groupId: Long, roles: List<String>) {
        GroupRoles.batchInsert(roles) {
            this[GroupRoles.groupId] = groupId
            this[GroupRoles.roleName] = it
        }
    }

    override fun batchInsertRoleToPermissions(roleName: String, permissionNames: List<String>) {
        RolePermissions.batchInsert(permissionNames) {
            this[RolePermissions.roleName] = roleName
            this[RolePermissions.permissionName] = it
        }
    }

    override fun listRoleByGroupId(groupId: Long): List<String> {
        return GroupRoles.select(GroupRoles.roleName)
            .where { GroupRoles.groupId eq groupId }
            .map { it[GroupRoles.roleName] }
    }

    override fun listPermissionByRole(role: String): List<String> {
        return RolePermissions.select(RolePermissions.permissionName)
            .where { RolePermissions.roleName eq role }
            .map { it[RolePermissions.permissionName] }
    }
}