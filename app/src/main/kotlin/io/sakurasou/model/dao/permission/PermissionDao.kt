package io.sakurasou.model.dao.permission

import io.sakurasou.model.dto.PermissionInsertDTO

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
interface PermissionDao {
    fun batchSavePermission(permissions: List<PermissionInsertDTO>)
}