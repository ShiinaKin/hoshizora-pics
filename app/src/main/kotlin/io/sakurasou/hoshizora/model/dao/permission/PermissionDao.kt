package io.sakurasou.hoshizora.model.dao.permission

import io.sakurasou.hoshizora.model.dto.PermissionInsertDTO
import io.sakurasou.hoshizora.model.entity.Permission

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
interface PermissionDao {
    fun batchSavePermission(permissions: List<PermissionInsertDTO>)

    fun findPermissionByName(name: String): Permission?

    fun findAllPermissions(): List<Permission>
}
