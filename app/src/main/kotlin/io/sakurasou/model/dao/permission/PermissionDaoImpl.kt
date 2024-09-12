package io.sakurasou.model.dao.permission

import io.sakurasou.model.dto.PermissionInsertDTO
import org.jetbrains.exposed.sql.batchInsert

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
class PermissionDaoImpl : PermissionDao {
    override fun batchSavePermission(permissions: List<PermissionInsertDTO>) {
        Permissions.batchInsert(permissions) {
            this[Permissions.name] = it.name
            this[Permissions.description] = it.description
        }
    }
}