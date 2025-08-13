package io.sakurasou.hoshizora.service.permission

import io.sakurasou.hoshizora.controller.vo.PermissionVO

/**
 * @author Shiina Kin
 * 2024/12/2 17:58
 */
interface PermissionService {
    suspend fun fetchAllPermissions(): List<PermissionVO>
}
