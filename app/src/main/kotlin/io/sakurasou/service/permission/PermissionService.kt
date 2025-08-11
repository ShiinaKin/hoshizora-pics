package io.sakurasou.service.permission

import io.sakurasou.controller.vo.PermissionVO

/**
 * @author Shiina Kin
 * 2024/12/2 17:58
 */
interface PermissionService {
    suspend fun fetchAllPermissions(): List<PermissionVO>
}
