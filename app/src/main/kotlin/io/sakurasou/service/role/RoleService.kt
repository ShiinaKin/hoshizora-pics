package io.sakurasou.service.role

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.request.RoleInsertRequest
import io.sakurasou.controller.request.RolePatchRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.RolePageVO
import io.sakurasou.controller.vo.RoleVO

/**
 * @author ShiinaKin
 * 2024/9/18 17:30
 */
interface RoleService {
    suspend fun saveRole(insertRequest: RoleInsertRequest)
    suspend fun patchRole(roleName: String, patchRequest: RolePatchRequest)
    suspend fun deleteRole(roleName: String)
    suspend fun fetchRole(roleName: String): RoleVO
    suspend fun pageRoles(pageRequest: PageRequest): PageResult<RolePageVO>
    suspend fun listRolesWithPermissionsOfUser(groupId: Long): List<RoleVO>
}