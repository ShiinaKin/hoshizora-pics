package io.sakurasou.model.dao.relation

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere

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

    override fun batchInsertPATToPermissions(patId: Long, permissionNames: List<String>) {
        PersonalAccessTokenPermissions.batchInsert(permissionNames) {
            this[PersonalAccessTokenPermissions.tokenId] = patId
            this[PersonalAccessTokenPermissions.permission] = it
        }
    }

    override fun deletePATToPermissionsByPATId(patId: Long) {
        PersonalAccessTokenPermissions.deleteWhere { tokenId eq patId }
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

    override fun listPermissionByPATId(patId: Long): List<String> {
        return PersonalAccessTokenPermissions.select(PersonalAccessTokenPermissions.permission)
            .where { PersonalAccessTokenPermissions.tokenId eq patId }
            .map { it[PersonalAccessTokenPermissions.permission] }
    }
}