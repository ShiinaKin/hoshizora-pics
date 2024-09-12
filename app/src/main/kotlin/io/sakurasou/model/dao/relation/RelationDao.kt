package io.sakurasou.model.dao.relation

/**
 * @author ShiinaKin
 * 2024/9/7 15:34
 */
interface RelationDao {
    fun batchInsertGroupToRoles(groupId: Long, roles: List<String>)
    fun batchInsertRoleToPermissions(roleName: String, permissionNames: List<String>)
    fun listRoleByGroupId(groupId: Long): List<String>
    fun listPermissionByRole(role: String): List<String>
}