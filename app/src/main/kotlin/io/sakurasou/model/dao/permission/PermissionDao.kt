package io.sakurasou.model.dao.permission

import io.sakurasou.model.dto.PermissionInsertDTO
import io.sakurasou.model.entity.Permission

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
interface PermissionDao {
    fun batchSavePermission(permissions: List<PermissionInsertDTO>)
    fun findPermissionByName(name: String): Permission?
    fun findAllPermissions(): List<Permission>
}