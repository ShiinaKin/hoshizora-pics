package io.sakurasou.service.role

import io.sakurasou.controller.vo.RoleVO

/**
 * @author ShiinaKin
 * 2024/9/18 17:30
 */
interface RoleService {
    suspend fun listRolesWithPermissions(): List<RoleVO>
    suspend fun listRolesWithPermissionsOfUser(groupId: Long): List<RoleVO>
}